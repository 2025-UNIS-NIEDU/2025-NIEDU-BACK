package com.niedu.service.edu;

import com.niedu.dto.course.*;
import com.niedu.entity.content.Content;
import com.niedu.entity.course.Session;
import com.niedu.entity.course.Step;
import com.niedu.entity.learning_record.SessionStatus;
import com.niedu.entity.learning_record.StudiedSession;
import com.niedu.entity.learning_record.StudiedStep;
import com.niedu.entity.user.User;
import com.niedu.repository.content.ContentRepository;
import com.niedu.repository.course.SessionRepository;
import com.niedu.repository.course.StepRepository;
import com.niedu.repository.learning_record.StudiedSessionRepository;
import com.niedu.repository.learning_record.StudiedStepRepository;
import com.niedu.service.edu.content.StepMapperService;
import com.niedu.service.edu.user_answer.UserAnswerMapperService;
import com.niedu.service.user.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SessionService {
    private final ContentRepository contentRepository;
    private final StepMapperService stepMapperService;
    private final UserAnswerMapperService userAnswerMapperService;
    private final AttendanceService attendanceService;
    private final SessionRepository sessionRepository;
    private final StudiedSessionRepository studiedSessionRepository;
    private final StepRepository stepRepository;
    private final StudiedStepRepository studiedStepRepository;

    public ArrayList<SessionListResponse> getSessions(Long courseId) {
        List<Session> sessions = sessionRepository.findAllByCourse_Id(courseId);
        ArrayList<SessionListResponse> responses = sessions.stream()
                .map(session -> new SessionListResponse(
                        session.getId(),
                        session.getNewsRef().getThumbnailUrl(),
                        session.getNewsRef().getHeadline(),
                        session.getNewsRef().getPublisher(),
                        session.getNewsRef().getPublishedAt()
                ))
                .collect(Collectors.toCollection(ArrayList::new));
        return responses;
    }

    public SessionStartResponse startSession(User user, Long sessionId, LevelRequest request) {

        // 0. 세션 존재 확인
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found: " + sessionId));

        // 학습 이력 존재 여부 확인
        StudiedSession sessionLog = studiedSessionRepository.findByUserAndSession_Id(user, sessionId);

        if (sessionLog == null) {
            // ---------- [처음 학습] ----------
            sessionLog = StudiedSession.builder()
                    .user(user)
                    .session(session)
                    .progress(0f)
                    .status(SessionStatus.IN_PROGRESS)
                    .startTime(LocalDateTime.now())
                    .build();

            StudiedSession savedStudiedSession = studiedSessionRepository.save(sessionLog);

            // Step 목록 조회
            List<Step> steps = stepRepository.findAllBySession_Id(sessionId);
            if (steps == null) {
                steps = List.of();
            }

            // StudiedStep 생성
            List<StudiedStep> studiedSteps = steps.stream()
                    .map(step -> StudiedStep.builder()
                            .user(user)
                            .step(step)
                            .isCompleted(false)
                            .build())
                    .toList();

            List<StudiedStep> savedStudiedSteps = studiedStepRepository.saveAll(studiedSteps);

            // StepListResponse 구성
            ArrayList<StepListResponse> stepListResponses = new ArrayList<>();
            for (StudiedStep studiedStep : studiedSteps) {
                Step step = studiedStep.getStep();
                if (step == null) continue;

                List<Content> contents = contentRepository.findAllByStep(step);
                if (contents == null) contents = List.of();

                stepListResponses.add(new StepListResponse(
                        step.getId(),
                        step.getStepOrder(),
                        studiedStep.getIsCompleted(),
                        step.getType(),
                        stepMapperService.toResponse(step, contents),
                        userAnswerMapperService.toResponse(studiedStep), // null 가능
                        userAnswerMapperService.checkIsCorrect(studiedStep) // null 가능
                ));
            }

            Long firstStudiedStepId = (savedStudiedSteps.isEmpty()) ? null : savedStudiedSteps.get(0).getId();

            return new SessionStartResponse(
                    firstStudiedStepId,
                    stepListResponses,
                    savedStudiedSession.getProgress()
            );
        }

        // ---------- [이전에 학습한 적 있음] ----------
        sessionLog.setStartTime(LocalDateTime.now());
        StudiedSession savedStudiedSession = studiedSessionRepository.save(sessionLog);

        // StudiedStep 불러오기
        List<StudiedStep> studiedSteps =
                studiedStepRepository.findAllByUser_IdAndStep_Session_Id(user.getId(), sessionId);

        if (studiedSteps == null) studiedSteps = List.of();

        ArrayList<StepListResponse> stepListResponses = new ArrayList<>();

        for (StudiedStep studiedStep : studiedSteps) {
            Step step = studiedStep.getStep();
            if (step == null) continue;

            List<Content> contents = contentRepository.findAllByStep(step);
            if (contents == null) contents = List.of();

            stepListResponses.add(new StepListResponse(
                    step.getId(),
                    step.getStepOrder(),
                    studiedStep.getIsCompleted(),
                    step.getType(),
                    stepMapperService.toResponse(step, contents),
                    userAnswerMapperService.toResponse(studiedStep),
                    userAnswerMapperService.checkIsCorrect(studiedStep)
            ));
        }

        // 첫 미완료 Step 찾기
        StudiedStep firstStep = studiedStepRepository
                .findFirstByUserAndStep_Session_IdAndIsCompletedFalseOrderByStep_IdAsc(user, sessionId)
                .orElseGet(() ->
                        studiedStepRepository.findFirstByUserAndStep_Session_Id(user, sessionId)
                );

        Long firstStudiedStepId = (firstStep != null) ? firstStep.getId() : null;

        return new SessionStartResponse(
                firstStudiedStepId,
                stepListResponses,
                savedStudiedSession.getProgress()
        );
    }

    public void quitSession(User user, Long sessionId) {
        // 1. 진행률 확인
        StudiedSession studiedSession = studiedSessionRepository.findByUserAndSession_Id(user, sessionId);
        List<StudiedStep> studiedSteps = studiedStepRepository.findAllByUser_IdAndStep_Session_Id(user.getId(), sessionId);
        long completedCount = studiedSteps.stream()
                .filter(StudiedStep::getIsCompleted)
                .count();

        float progress = ((float) completedCount / studiedSteps.size()) * 100; // % 단위

        // 소수점 한 자리까지만 유지 (nn.n)
        float roundedProgress = Math.round(progress * 10f) / 10f;

        // 2. StudiedSession 진행률 업데이트
        studiedSession.setProgress(roundedProgress);
    }

    public SessionSummaryResponse summarizeSession(User user, Long sessionId) {
        // 1. streak 조회
        int streak = attendanceService.calculateStreak(user.getId());
        // 2. learningTime 조회
        StudiedSession studiedSession = studiedSessionRepository.findByUserAndSession_Id(user, sessionId);
        Duration learningTime = studiedSession.getStudiedTime();
        // 3. 리턴
        return new SessionSummaryResponse(streak, learningTime);
    }
}
