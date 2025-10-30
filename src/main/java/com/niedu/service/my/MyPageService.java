package com.niedu.service.my;

import com.niedu.dto.my.*;
import com.niedu.dto.course.content.KeywordContent;
import com.niedu.dto.course.content.MultipleChoiceContentResponse;
import com.niedu.dto.course.content.OxQuizContentResponse;
import com.niedu.dto.course.content.SessionReflectionContentResponse;
import com.niedu.dto.my.SentenceCompletionReviewRecord;
import com.niedu.dto.my.ShortAnswerReviewRecord;
import com.niedu.dto.my.SummaryReadingReviewRecord;
import com.niedu.entity.learning_record.user_answer.SimpleAnswer;
import com.niedu.entity.content.*;
import com.niedu.entity.course.Session;
import com.niedu.entity.course.Step;
import com.niedu.entity.course.StepType;
import com.niedu.entity.learning_record.SavedTerm;
import com.niedu.entity.learning_record.SharedResponse;
import com.niedu.entity.learning_record.StudiedSession;
import com.niedu.entity.learning_record.StudiedStep;
import com.niedu.entity.learning_record.user_answer.SentenceCompletionAnswer;
import com.niedu.entity.learning_record.user_answer.SummaryReadingAnswer;
import com.niedu.entity.learning_record.user_answer.UserAnswer;
import com.niedu.entity.topic.Topic;
import com.niedu.entity.user.User;
import com.niedu.repository.content.ContentRepository;
import com.niedu.repository.content.TermRepository;
import com.niedu.repository.learning_record.SavedTermRepository;
import com.niedu.repository.learning_record.SharedResponseRepository;
import com.niedu.repository.learning_record.StudiedSessionRepository;
import com.niedu.repository.learning_record.UserAnswerRepository;
import com.niedu.repository.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MyPageService {

    private final UserRepository userRepository;
    private final SavedTermRepository savedTermRepository;
    private final TermRepository termRepository;
    private final StudiedSessionRepository studiedSessionRepository;
    private final SharedResponseRepository sharedResponseRepository;
    private final UserAnswerRepository userAnswerRepository;
    private final ContentRepository contentRepository;

    private static final char SHORT = 0x3138;

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
                if (coursesForThisDay.size() < 3) {
                    Topic topic = ss.getSession().getCourse().getTopic();
                    String topicName = topic.getName();
                    String subTopicName = ss.getSession().getNewsRef().getHeadline();
                    coursesForThisDay.add(MyCalendarCourseInfo.fromTopic(topicName, subTopicName));
                } else {
                    extraCount++;
                }
            }

            for (SharedResponse sr : responsesToday) {
                if (coursesForThisDay.size() < 3) {
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

        for (UserAnswer answer : answers) {
            Content content = answer.getContent();
            StudiedStep studiedStep = answer.getStudiedStep();
            Step step = studiedStep.getStep();
            Topic topic = step.getSession().getCourse().getTopic();
            StepType stepType = step.getType();
            String contentType = stepType.name();
            String level = convertStepTypeToLevel(stepType);

            Object contentDto = null;
            try {
                switch (contentType) {
                    case "OX_QUIZ":
                        contentDto = OxQuizContentResponse.fromEntity((OxQuiz) content);
                        break;
                    case "MULTIPLE_CHOICE":
                        contentDto = MultipleChoiceContentResponse.fromEntity((MultipleChoiceQuiz) content);
                        break;
                    case "SENTENCE_COMPLETION":
                        contentDto = SentenceCompletionReviewRecord.fromEntities(
                                (SentenceCompletionQuiz) content,
                                (SentenceCompletionAnswer) answer
                        );
                        break;
                    case "SHORT_ANSWER":
                        contentDto = ShortAnswerReviewRecord.fromEntities(
                                (ShortAnswerQuiz) content,
                                (SimpleAnswer) answer
                        );
                        break;
                    case "SUMMARY_READING":
                        contentDto = SummaryReadingReviewRecord.fromEntities(
                                (SummaryReading) content,
                                (SummaryReadingAnswer) answer
                        );
                        break;
                    default:
                        log.warn("getReviewNotes: 처리되지 않은 퀴즈 contentType [{}] (Content ID: {})", contentType, content.getId());
                        break;
                }
            } catch (ClassCastException e) {
                log.error("Content (ID: {})와 StepType ({})이 일치하지 않습니다.", content.getId(), contentType, e);
                continue;
            }

            Long currentUserId = studiedStep.getUser().getId();
            Long currentSessionId = step.getSession().getId();
            StudiedSession studiedSession = studiedSessionRepository
                    .findByUser_IdAndSession_Id(currentUserId, currentSessionId);

            LocalDateTime createdAt = (studiedSession != null) ? studiedSession.getStartTime() : null;

            if (createdAt != null && contentDto != null) {
                reviewNotes.add(new ReviewNoteItemResponse(topic.getName(), level, contentType, contentDto, createdAt));
            }
        }

        for (SharedResponse response : responses) {
            Step step = response.getStep();
            Topic topic = step.getSession().getCourse().getTopic();
            StepType stepType = step.getType();
            String contentType = stepType.name();
            String level = convertStepTypeToLevel(stepType);
            LocalDateTime createdAt = response.getCreatedAt();

            String reflectionQuestion = "질문을 불러올 수 없습니다.";
            try {
                List<Content> contents = contentRepository.findAllByStep(step);
                if (!contents.isEmpty() && contents.get(0) instanceof SessionReflection) {
                    reflectionQuestion = ((SessionReflection) contents.get(0)).getQuestion();
                } else if (!contents.isEmpty()) {
                    log.warn("StepType(SESSION_REFLECTION)과 ContentType이 일치하지 않습니다. Step ID: {}", step.getId());
                } else {
                    log.warn("Step(ID: {})에 연결된 SESSION_REFLECTION Content가 없습니다.", step.getId());
                }
            } catch (Exception e) {
                log.error("SESSION_REFLECTION 질문을 찾는 중 오류 발생. Step ID: {}", step.getId(), e);
            }
            Object contentDto = new SessionReflectionContentResponse(reflectionQuestion);

            reviewNotes.add(new ReviewNoteItemResponse(topic.getName(), level, contentType, contentDto, createdAt));
        }

        reviewNotes.sort(Comparator.comparing(ReviewNoteItemResponse::createdAt).reversed());

        return reviewNotes;
    }

    public MyTermListResponse getAllMyTerms(Long userId, String sort) {

        List<TermGroupRecord> groups = new ArrayList<>();

        if ("alphabetical".equals(sort)) {
            List<SavedTerm> savedTerms = savedTermRepository.findByUser_IdOrderByTerm_NameAsc(userId);

            List<TermItemRecord> termItems = savedTerms.stream()
                    .map(st -> new TermItemRecord(st.getTerm().getId(), st.getTerm().getName()))
                    .toList();

            Map<String, List<TermItemRecord>> groupedByInitial = termItems.stream()
                    .collect(Collectors.groupingBy(
                            item -> getInitial(item.term()),
                            LinkedHashMap::new,
                            Collectors.toList()
                    ));

            groups = groupedByInitial.entrySet().stream()
                    .map(entry -> TermGroupRecord.forAlphabetical(entry.getKey(), entry.getValue()))
                    .toList();

        } else { // "recent"
            List<SavedTerm> savedTerms = savedTermRepository.findByUser_IdOrderBySavedAtDesc(userId);

            LocalDate today = LocalDate.now();
            LocalDate yesterday = today.minusDays(1);
            LocalDate sevenDaysAgo = today.minusDays(7);

            List<TermItemRecord> todayTerms = new ArrayList<>();
            List<TermItemRecord> yesterdayTerms = new ArrayList<>();
            List<TermItemRecord> last7DaysTerms = new ArrayList<>();
            List<TermItemRecord> otherTerms = new ArrayList<>();

            for (SavedTerm st : savedTerms) {
                if (st.getSavedAt() == null) continue;
                LocalDate savedDate = st.getSavedAt().toLocalDate();
                TermItemRecord item = new TermItemRecord(st.getTerm().getId(), st.getTerm().getName());

                if (savedDate.isEqual(today)) {
                    todayTerms.add(item);
                } else if (savedDate.isEqual(yesterday)) {
                    yesterdayTerms.add(item);
                } else if (savedDate.isAfter(sevenDaysAgo) && savedDate.isBefore(yesterday)) {
                    last7DaysTerms.add(item);
                } else {
                    otherTerms.add(item);
                }
            }

            if (!todayTerms.isEmpty()) {
                groups.add(TermGroupRecord.forRecent("오늘", todayTerms));
            }
            if (!yesterdayTerms.isEmpty()) {
                groups.add(TermGroupRecord.forRecent("어제", yesterdayTerms));
            }
            if (!last7DaysTerms.isEmpty()) {
                groups.add(TermGroupRecord.forRecent("최근 7일", last7DaysTerms));
            }
            if (!otherTerms.isEmpty()) {
                groups.add(TermGroupRecord.forRecent("그 외", otherTerms));
            }
        }

        return new MyTermListResponse(groups);
    }

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

    private String convertStepTypeToLevel(StepType type) {
        if (type == null) {
            return "N";
        }

        switch (type) {
            case OX_QUIZ:
            case TERM_LEARNING:
            case CURRENT_AFFAIRS:
                return "N";

            case MULTIPLE_CHOICE:
            case ARTICLE_READING:
            case SHORT_ANSWER:
            case SUMMARY_READING:
                return "I";

            case SESSION_REFLECTION:
            case SENTENCE_COMPLETION:
                return "E";

            default:
                return "N";
        }
    }

    private String getInitial(String text) {
        if (text == null || text.isEmpty()) {
            return "?";
        }
        char firstChar = text.charAt(0);

        if (firstChar >= '가' && firstChar <= '힣') {
            final char[] initials = {
                    'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ', SHORT,
                    'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
            };
            int index = (firstChar - '가') / (21 * 28);
            return String.valueOf(initials[index]);
        }
        if ((firstChar >= 'A' && firstChar <= 'Z') || (firstChar >= 'a' && firstChar <= 'z')) {
            return String.valueOf(Character.toUpperCase(firstChar));
        }
        return "#";
    }
}