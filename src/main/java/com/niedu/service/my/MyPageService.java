package com.niedu.service.my;

import com.niedu.dto.my.*;
import com.niedu.entity.content.Content;
import com.niedu.entity.content.Term;
import com.niedu.entity.course.Session;
import com.niedu.entity.course.Step;
import com.niedu.entity.course.StepType;
import com.niedu.entity.learning_record.SharedResponse;
import com.niedu.entity.learning_record.StudiedSession;
import com.niedu.entity.learning_record.StudiedStep;
import com.niedu.entity.learning_record.user_answer.UserAnswer;
import com.niedu.entity.topic.Topic;
import com.niedu.entity.topic.SubTopic;
import com.niedu.entity.user.User;
import com.niedu.repository.content.TermRepository;
import com.niedu.repository.learning_record.SavedTermRepository;
import com.niedu.repository.learning_record.SharedResponseRepository;
import com.niedu.repository.learning_record.StudiedSessionRepository;
import com.niedu.repository.learning_record.UserAnswerRepository;
import com.niedu.repository.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator; // Comparator import
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyPageService {

    private final UserRepository userRepository;
    private final SavedTermRepository savedTermRepository;
    private final TermRepository termRepository;
    private final StudiedSessionRepository studiedSessionRepository;
    private final SharedResponseRepository sharedResponseRepository;
    private final UserAnswerRepository userAnswerRepository;

    public MyCalendarResponse getDateNavigator(Long userId) {
        LocalDate now = LocalDate.now();
        return getCalendar(userId, now.getYear(), now.getMonthValue());
    }

    public MyCalendarResponse getCalendar(Long userId, Integer year, Integer month) {
        LocalDate firstDayOfMonth = LocalDate.of(year, month, 1);
        LocalDateTime monthStart = firstDayOfMonth.atStartOfDay();
        LocalDateTime monthEnd = firstDayOfMonth.with(TemporalAdjusters.lastDayOfMonth()).atTime(LocalTime.MAX);
        int daysInMonth = firstDayOfMonth.lengthOfMonth();

        List<StudiedSession> sessions = studiedSessionRepository.findByUserIdAndStartTimeBetween(userId, monthStart, monthEnd);
        List<SharedResponse> responses = sharedResponseRepository.findByUserIdAndCreatedAtBetween(userId, monthStart, monthEnd);

        Map<LocalDate, List<StudiedSession>> sessionsByDate = sessions.stream()
                .collect(Collectors.groupingBy(s -> s.getStartTime().toLocalDate()));
        Map<LocalDate, List<SharedResponse>> responsesByDate = responses.stream()
                .collect(Collectors.groupingBy(r -> r.getCreatedAt().toLocalDate()));

        List<MyCalendarResponse.DayDetail> days = new ArrayList<>();

        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate currentDate = firstDayOfMonth.withDayOfMonth(day);
            LocalDateTime currentDateTime = currentDate.atStartOfDay();

            List<MyCalendarCourseInfo> coursesForThisDay = new ArrayList<>();
            int extraCount = 0;

            List<StudiedSession> sessionsToday = sessionsByDate.getOrDefault(currentDate, Collections.emptyList());
            List<SharedResponse> responsesToday = responsesByDate.getOrDefault(currentDate, Collections.emptyList());

            for (StudiedSession ss : sessionsToday) {
                if (coursesForThisDay.size() < 2) {
                    Topic topic = ss.getSession().getCourse().getTopic();
                    String topicName = topic.getName();
                    String subTopicName = ss.getSession().getNewsRef().getHeadline();
                    coursesForThisDay.add(MyCalendarCourseInfo.fromTopic(topicName, subTopicName));
                } else {
                    extraCount++;
                }
            }

            for (SharedResponse sr : responsesToday) {
                if (coursesForThisDay.size() < 2) {
                    Topic topic = sr.getStep().getSession().getCourse().getTopic();
                    String topicName = topic.getName();
                    String subTopicName = sr.getStep().getType().name();
                    coursesForThisDay.add(MyCalendarCourseInfo.fromTopic(topicName, subTopicName));
                } else {
                    extraCount++;
                }
            }

            if (extraCount > 0) {
                coursesForThisDay.add(MyCalendarCourseInfo.fromExtra(extraCount));
            }

            days.add(new MyCalendarResponse.DayDetail(currentDateTime, coursesForThisDay));
        }

        return new MyCalendarResponse(year, month, days);
    }

    /**
     * 복습 노트 내 날짜별 문제 조회 (SET-REVIEW-01~03)
     * [수정됨] 퀴즈(UserAnswer) 및 세션돌아보기(SharedResponse) 조회 로직 구현
     */
    public List<ReviewNoteItemResponse> getReviewNotes(Long userId, LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        List<UserAnswer> answers = userAnswerRepository
                .findUserAnswersByDateRange(userId, startOfDay, endOfDay);
        List<SharedResponse> responses = sharedResponseRepository
                .findByUserIdAndCreatedAtBetween(userId, startOfDay, endOfDay);

        List<ReviewNoteItemResponse> reviewNotes = new ArrayList<>();

        // 3-1. UserAnswer 목록 변환
        for (UserAnswer answer : answers) {
            Content content = answer.getContent();
            StudiedStep studiedStep = answer.getStudiedStep();
            Step step = studiedStep.getStep();
            Topic topic = step.getSession().getCourse().getTopic();

            // --- 수정: StepType과 Level을 Step에서 가져옴 ---
            StepType stepType = step.getType();
            String contentType = stepType.name(); // (예: "OX_QUIZ")

            // --- 수정: 헬퍼 메서드를 사용해 Level 변환 ---
            String level = convertStepTypeToLevel(stepType); // (예: "N")

            // TODO: contentType에 따라 분기하여 실제 퀴즈 DTO를 생성해야 합니다.
            Object contentDto = null;
            // switch (contentType) { ... }

            Long currentUserId = studiedStep.getUser().getId();
            Long currentSessionId = step.getSession().getId();
            StudiedSession studiedSession = studiedSessionRepository
                    .findByUser_IdAndSession_Id(currentUserId, currentSessionId);

            LocalDateTime createdAt = (studiedSession != null) ? studiedSession.getStartTime() : null;

            if (createdAt != null) {
                reviewNotes.add(new ReviewNoteItemResponse(topic.getName(), level, contentType, contentDto, createdAt));
            }
        }

        // 3-2. SharedResponse 목록 변환
        for (SharedResponse response : responses) {
            Step step = response.getStep();
            Topic topic = step.getSession().getCourse().getTopic();

            // --- 수정: StepType과 Level을 Step에서 가져옴 ---
            StepType stepType = step.getType();
            String contentType = stepType.name(); // "SESSION_REFLECTION"

            // --- 수정: 헬퍼 메서드를 사용해 Level 변환 ---
            String level = convertStepTypeToLevel(stepType); // (예: "E")
            LocalDateTime createdAt = response.getCreatedAt();
            Object contentDto = new SharedResponseContent(response.getUserResponse());

            reviewNotes.add(new ReviewNoteItemResponse(topic.getName(), level, contentType, contentDto, createdAt));
        }

        // --- 4. 두 리스트를 합쳐서 시간순으로 정렬 (최신순) ---
        reviewNotes.sort(Comparator.comparing(ReviewNoteItemResponse::createdAt).reversed());

        return reviewNotes;
    }

    /**
     * 용어 사전 내 전체 용어 조회 (SET-DICTIONARY-03)
     * [구현 안 됨]
     */
    public MyTermListResponse getAllMyTerms(Long userId, String sort) {
        // --- 구현 안 됨 ---
        List<TermGroupRecord> groups = new ArrayList<>();
        if ("alphabetical".equals(sort)) {
            // TODO: 로직 구현 (SavedTermRepository에서 가나다순 조회 -> 초성 그룹핑)
        } else { // "recent"
            // TODO: 로M: 로직 구현 (SavedTermRepository에서 최신순 조회 -> 날짜(오늘/어제/7일) 그룹핑)
        }
        return new MyTermListResponse(groups);
    }

    /**
     * 용어 사전 내 특정 용어 조회 (SET-DICTIONARY-04)
     * [구현 완료]
     */
    public MyTermDetailResponse getMyTermById(Long userId, Long termId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        Term term = termRepository.findById(termId)
                .orElseThrow(() -> new EntityNotFoundException("Term not found with id: " + termId));
        if (!savedTermRepository.existsByUserAndTerm(user, term)) {
            throw new EntityNotFoundException("사용자가 저장한 용어사전에서 해당 용어를 찾을 수 없습니다.");
        }
        return MyTermDetailResponse.fromEntity(term);
    }
}