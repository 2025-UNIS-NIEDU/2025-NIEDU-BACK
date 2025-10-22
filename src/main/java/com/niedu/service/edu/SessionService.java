package com.niedu.service.edu;

import com.niedu.dto.course.*;
import com.niedu.entity.course.Session;
import com.niedu.entity.course.Step;
import com.niedu.entity.learning_record.SessionStatus;
import com.niedu.entity.learning_record.StudiedSession;
import com.niedu.entity.learning_record.StudiedStep;
import com.niedu.entity.user.AttendanceLog;
import com.niedu.entity.user.User;
import com.niedu.repository.course.SessionRepository;
import com.niedu.repository.course.StepRepository;
import com.niedu.repository.learning_record.StudiedSessionRepository;
import com.niedu.repository.learning_record.StudiedStepRepository;
import com.niedu.repository.user.AttendanceLogRepository;
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
    private final StepMapperService stepMapperService;
    private final UserAnswerMapperService userAnswerMapperService;
    private final AttendanceService attendanceService;
    private final SessionRepository sessionRepository;
    private final StudiedSessionRepository studiedSessionRepository;
    private final StepRepository stepRepository;
    private final StudiedStepRepository studiedStepRepository;
    private final AttendanceLogRepository attendanceLogRepository;

    public ArrayList<SessionListResponse> getSessions(Long courseId) {
        List<Session> sessions = sessionRepository.findAllByCourse_Id(courseId);
        ArrayList<SessionListResponse> responses = sessions.stream()
                .map(session -> new SessionListResponse(
                        session.getNewsRef().getThumbnailUrl(),
                        session.getNewsRef().getHeadline(),
                        session.getNewsRef().getPublisher(),
                        session.getNewsRef().getPublishedAt()
                ))
                .collect(Collectors.toCollection(ArrayList::new));
        return responses;
    }

    public SessionStartResponse startSession(User user, Long sessionId, LevelRequest request) {
        // 학습 이력 존재 여부를 바탕으로 StudiedSession 생성/업데이트
        StudiedSession sessionLog = studiedSessionRepository.findByUser_IdAndSession_Id(user.getId(), sessionId);
        if (sessionLog == null) { // 처음 학습하는 경우
            // 1. StudiedSession 초기화
            sessionLog = StudiedSession.builder()
                    .user(user)
                    .session(sessionRepository.findById(sessionId).orElse(null))
                    .progress(0f)
                    .status(SessionStatus.IN_PROGRESS)
                    .startTime(LocalDateTime.now())
                    .build();
            StudiedSession savedStudiedSession = studiedSessionRepository.save(sessionLog);
            // 2. StudiedStep 초기화
            List<Step> steps = stepRepository.findAllBySession_Id(sessionId);
            List<StudiedStep> studiedSteps = steps.stream()
                    .map(step -> StudiedStep.builder()
                            .user(user)
                            .step(step)
                            .isCompleted(false)
                            .build())
                    .toList();
            List<StudiedStep> savedStudiedSteps = studiedStepRepository.saveAll(studiedSteps);
            // 3. steps 세팅
            ArrayList<StepListResponse> stepListResponses = studiedSteps.stream()
                    .map(studiedStep -> {
                        Step step = studiedStep.getStep();
                        return new StepListResponse(
                                step.getId(),
                                step.getStepOrder(),
                                studiedStep.getIsCompleted(),
                                step.getType(),
                                stepMapperService.toResponse(step),
                                userAnswerMapperService.toResponse(step)
                        );
                    })
                    .collect(Collectors.toCollection(ArrayList::new));
            // 4. record 생성해서 리턴
            SessionStartResponse response = new SessionStartResponse(
                    savedStudiedSteps.getFirst().getId(),
                    stepListResponses,
                    savedStudiedSession.getProgress()
            );
        }
        else {
            // 이전 학습 경험이 있는 경우
            // 1. StudiedSession 업데이트
            sessionLog.setStartTime(LocalDateTime.now());
            StudiedSession savedStudiedSession = studiedSessionRepository.save(sessionLog);
            // 2. StudiedStep 불러오기
            List<StudiedStep> studiedSteps = studiedStepRepository.findAllByUser_IdAndSession_Id(user.getId(), sessionId);
            // 3. steps 세팅
            ArrayList<StepListResponse> stepListResponses = studiedSteps.stream()
                    .map(studiedStep -> {
                        Step step = studiedStep.getStep();
                        return new StepListResponse(
                                step.getId(),
                                step.getStepOrder(),
                                studiedStep.getIsCompleted(),
                                step.getType(),
                                stepMapperService.toResponse(step),
                                userAnswerMapperService.toResponse(step)
                        );
                    })
                    .collect(Collectors.toCollection(ArrayList::new));
            // 4. record 생성해서 리턴
            StudiedStep firstStep = studiedStepRepository
                    .findFirstByUserAndStep_Session_IdAndIsCompletedFalseOrderByStep_IdAsc(user, sessionId)
                    .orElseGet(() -> studiedStepRepository.findFirstByUserAndStep_Session_Id(user, sessionId));
            SessionStartResponse response = new SessionStartResponse(
                    firstStep.getId(),
                    stepListResponses,
                    savedStudiedSession.getProgress()
            );
        }
        return null;
    }

    public void quitSession(User user, Long sessionId) {
        // 1. 진행률 확인
        StudiedSession studiedSession = studiedSessionRepository.findByUser_IdAndSession_Id(user.getId(), sessionId);
        List<StudiedStep> studiedSteps = studiedStepRepository.findAllByUser_IdAndSession_Id(user.getId(), sessionId);
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
        StudiedSession studiedSession = studiedSessionRepository.findByUser_IdAndSession_Id(user.getId(), sessionId);
        Duration learningTime = studiedSession.getStudiedTime();
        // 3. 리턴
        return new SessionSummaryResponse(streak, learningTime);
    }
}
