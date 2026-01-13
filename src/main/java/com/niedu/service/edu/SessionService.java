package com.niedu.service.edu;

import com.niedu.dto.course.*;
import com.niedu.entity.content.Content;
import com.niedu.entity.course.Level;
import com.niedu.entity.course.Course;
import com.niedu.entity.course.Session;
import com.niedu.entity.course.Step;
import com.niedu.entity.learning_record.SessionStatus;
import com.niedu.entity.learning_record.StudiedCourse;
import com.niedu.entity.learning_record.StudiedSession;
import com.niedu.entity.learning_record.StudiedStep;
import com.niedu.entity.user.User;
import com.niedu.repository.content.ContentRepository;
import com.niedu.repository.course.SessionRepository;
import com.niedu.repository.course.StepRepository;
import com.niedu.repository.learning_record.StudiedSessionRepository;
import com.niedu.repository.learning_record.StudiedStepRepository;
import com.niedu.repository.learning_record.StudiedCourseRepository;
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
    private final StudiedCourseRepository studiedCourseRepository;

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

        Level selectedLevel = parseLevel(request);

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
            upsertStudiedCourse(user, session.getCourse());

            // Step 목록 조회
            List<Step> steps = stepRepository.findAllBySession_IdOrderByStepOrderAsc(sessionId);
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
            List<StudiedStep> filteredStudiedSteps = filterByLevel(savedStudiedSteps, selectedLevel);
            for (StudiedStep studiedStep : filteredStudiedSteps) {
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

            Long firstStudiedStepId = findFirstStudiedStepId(filteredStudiedSteps);

            return new SessionStartResponse(
                    firstStudiedStepId,
                    stepListResponses,
                    savedStudiedSession.getProgress()
            );
        }

        // ---------- [이전에 학습한 적 있음] ----------
        sessionLog.setStartTime(LocalDateTime.now());
        StudiedSession savedStudiedSession = studiedSessionRepository.save(sessionLog);
        upsertStudiedCourse(user, session.getCourse());

        // StudiedStep 불러오기
        List<StudiedStep> studiedSteps =
                studiedStepRepository.findAllByUser_IdAndStep_Session_Id(user.getId(), sessionId);

        if (studiedSteps == null) studiedSteps = List.of();

        ArrayList<StepListResponse> stepListResponses = new ArrayList<>();
        List<StudiedStep> filteredStudiedSteps = filterByLevel(studiedSteps, selectedLevel);
        for (StudiedStep studiedStep : filteredStudiedSteps) {
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

        Long firstStudiedStepId = findFirstStudiedStepId(filteredStudiedSteps);

        return new SessionStartResponse(
                firstStudiedStepId,
                stepListResponses,
                savedStudiedSession.getProgress()
        );
    }

    private Level parseLevel(LevelRequest request) {
        if (request == null || request.level() == null || request.level().isBlank()) {
            return null;
        }
        try {
            return Level.valueOf(request.level().trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException("Invalid level: " + request.level());
        }
    }

    private List<StudiedStep> filterByLevel(List<StudiedStep> studiedSteps, Level selectedLevel) {
        if (selectedLevel == null) {
            return sortByStepOrder(studiedSteps);
        }
        return studiedSteps.stream()
                .filter(studiedStep -> {
                    Step step = studiedStep.getStep();
                    return step != null && selectedLevel.equals(step.getLevel());
                })
                .sorted(this::compareByStepOrderThenId)
                .toList();
    }

    private Long findFirstStudiedStepId(List<StudiedStep> studiedSteps) {
        if (studiedSteps == null || studiedSteps.isEmpty()) {
            return null;
        }
        return studiedSteps.stream()
                .sorted((a, b) -> {
                    Integer aOrder = a.getStep() != null ? a.getStep().getStepOrder() : null;
                    Integer bOrder = b.getStep() != null ? b.getStep().getStepOrder() : null;
                    if (aOrder == null && bOrder == null) return 0;
                    if (aOrder == null) return 1;
                    if (bOrder == null) return -1;
                    int orderCompare = aOrder.compareTo(bOrder);
                    if (orderCompare != 0) return orderCompare;
                    Long aId = a.getStep() != null ? a.getStep().getId() : null;
                    Long bId = b.getStep() != null ? b.getStep().getId() : null;
                    if (aId == null && bId == null) return 0;
                    if (aId == null) return 1;
                    if (bId == null) return -1;
                    return aId.compareTo(bId);
                })
                .filter(studiedStep -> Boolean.FALSE.equals(studiedStep.getIsCompleted()))
                .map(studiedStep -> studiedStep.getStep() != null ? studiedStep.getStep().getId() : null)
                .findFirst()
                .or(() -> studiedSteps.stream()
                        .sorted((a, b) -> {
                            Integer aOrder = a.getStep() != null ? a.getStep().getStepOrder() : null;
                            Integer bOrder = b.getStep() != null ? b.getStep().getStepOrder() : null;
                            if (aOrder == null && bOrder == null) return 0;
                            if (aOrder == null) return 1;
                            if (bOrder == null) return -1;
                            int orderCompare = aOrder.compareTo(bOrder);
                            if (orderCompare != 0) return orderCompare;
                            Long aId = a.getStep() != null ? a.getStep().getId() : null;
                            Long bId = b.getStep() != null ? b.getStep().getId() : null;
                            if (aId == null && bId == null) return 0;
                            if (aId == null) return 1;
                            if (bId == null) return -1;
                            return aId.compareTo(bId);
                        })
                        .map(studiedStep -> studiedStep.getStep() != null ? studiedStep.getStep().getId() : null)
                        .findFirst()
                )
                .orElse(null);
    }

    private List<StudiedStep> sortByStepOrder(List<StudiedStep> studiedSteps) {
        if (studiedSteps == null) {
            return List.of();
        }
        return studiedSteps.stream()
                .sorted(this::compareByStepOrderThenId)
                .toList();
    }

    private int compareByStepOrderThenId(StudiedStep a, StudiedStep b) {
        Integer aOrder = a.getStep() != null ? a.getStep().getStepOrder() : null;
        Integer bOrder = b.getStep() != null ? b.getStep().getStepOrder() : null;
        if (aOrder == null && bOrder == null) return 0;
        if (aOrder == null) return 1;
        if (bOrder == null) return -1;
        int orderCompare = aOrder.compareTo(bOrder);
        if (orderCompare != 0) return orderCompare;
        Long aId = a.getStep() != null ? a.getStep().getId() : null;
        Long bId = b.getStep() != null ? b.getStep().getId() : null;
        if (aId == null && bId == null) return 0;
        if (aId == null) return 1;
        if (bId == null) return -1;
        return aId.compareTo(bId);
    }

    public void quitSession(User user, Long sessionId) {
        // 1. 진행률 확인
        StudiedSession studiedSession = studiedSessionRepository.findByUserAndSession_Id(user, sessionId);
        if (studiedSession == null) {
            throw new RuntimeException("StudiedSession not found for user " + user.getId() + " and session " + sessionId);
        }
        List<StudiedStep> studiedSteps = studiedStepRepository.findAllByUser_IdAndStep_Session_Id(user.getId(), sessionId);
        long completedCount = studiedSteps.stream()
                .filter(StudiedStep::getIsCompleted)
                .count();

        float progress = 0f;
        if (!studiedSteps.isEmpty()) {
            progress = ((float) completedCount / studiedSteps.size()) * 100; // % 단위
        }

        // 소수점 한 자리까지만 유지 (nn.n)
        float roundedProgress = Math.round(progress * 10f) / 10f;

        // 2. StudiedSession 진행률 업데이트
        studiedSession.setProgress(roundedProgress);
        studiedSessionRepository.save(studiedSession);
        upsertStudiedCourse(user, studiedSession.getSession().getCourse());
    }

    private void upsertStudiedCourse(User user, Course course) {
        if (course == null) {
            return;
        }

        long totalSessions = sessionRepository.countByCourse_Id(course.getId());
        float progress = 0f;
        if (totalSessions > 0) {
            List<StudiedSession> studiedSessions =
                    studiedSessionRepository.findAllByUserAndSession_Course_Id(user, course.getId());
            float sumProgress = studiedSessions.stream()
                    .map(StudiedSession::getProgress)
                    .filter(p -> p != null)
                    .reduce(0f, Float::sum);
            progress = sumProgress / totalSessions;
        }

        StudiedCourse studiedCourse = studiedCourseRepository.findByUserAndCourse_Id(user, course.getId());
        if (studiedCourse == null) {
            studiedCourse = StudiedCourse.builder()
                    .user(user)
                    .course(course)
                    .progress(progress)
                    .build();
        } else {
            studiedCourse.setProgress(progress);
        }

        studiedCourseRepository.save(studiedCourse);
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
