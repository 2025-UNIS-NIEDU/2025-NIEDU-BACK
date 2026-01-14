package com.niedu.service.my;

import com.niedu.dto.my.*;
import com.niedu.dto.course.content.*;
import com.niedu.dto.course.user_answer.*;

import com.niedu.entity.content.*;
import com.niedu.entity.course.Level;
import com.niedu.entity.course.Step;
import com.niedu.entity.course.StepType;
import com.niedu.entity.learning_record.*;
import com.niedu.entity.learning_record.user_answer.*;
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
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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

    public MyCalendarResponse getDateNavigator(User user) {
        LocalDate now = LocalDate.now();
        return getCalendar(user, now.getYear(), now.getMonthValue());
    }

    public MyCalendarResponse getCalendar(User user, Integer year, Integer month) {
        LocalDate firstDayOfMonth = LocalDate.of(year, month, 1);
        LocalDateTime monthStart = firstDayOfMonth.atStartOfDay();
        LocalDateTime monthEnd = firstDayOfMonth.with(TemporalAdjusters.lastDayOfMonth()).atTime(LocalTime.MAX);
        int daysInMonth = firstDayOfMonth.lengthOfMonth();

        List<StudiedSession> sessions = studiedSessionRepository.findByUserAndStartTimeBetween(user, monthStart, monthEnd);
        List<SharedResponse> responses = sharedResponseRepository.findByUserAndCreatedAtBetween(user, monthStart, monthEnd);

        Map<LocalDate, List<StudiedSession>> sessionsByDate = sessions.stream()
                .collect(Collectors.groupingBy(s -> s.getStartTime().toLocalDate()));
        Map<LocalDate, List<SharedResponse>> responsesByDate = responses.stream()
                .collect(Collectors.groupingBy(r -> r.getCreatedAt().toLocalDate()));

        List<MyCalendarResponse.DayDetail> days = IntStream.rangeClosed(1, daysInMonth)
                .mapToObj(day -> {
                    LocalDate currentDate = firstDayOfMonth.withDayOfMonth(day);
                    List<StudiedSession> sessionsToday = sessionsByDate.getOrDefault(currentDate, Collections.emptyList());
                    List<SharedResponse> responsesToday = responsesByDate.getOrDefault(currentDate, Collections.emptyList());

                    List<MyCalendarCourseInfo> coursesForThisDay = extractCoursesForCalendar(sessionsToday, responsesToday);
                    return new MyCalendarResponse.DayDetail(currentDate.atStartOfDay(), coursesForThisDay);
                })
                .toList();

        return new MyCalendarResponse(year, month, days);
    }

    private List<MyCalendarCourseInfo> extractCoursesForCalendar(List<StudiedSession> sessions, List<SharedResponse> responses) {
        Stream<MyCalendarCourseInfo> sessionCourses = sessions.stream()
                .map(ss -> {
                    Topic topic = ss.getSession().getCourse().getTopic();
                    String subTopicName = ss.getSession().getNewsRef().getHeadline();
                    return MyCalendarCourseInfo.fromTopic(topic.getName(), subTopicName);
                });

        Stream<MyCalendarCourseInfo> responseCourses = responses.stream()
                .map(sr -> {
                    Topic topic = sr.getStep().getSession().getCourse().getTopic();
                    String subTopicName = sr.getStep().getType().name();
                    return MyCalendarCourseInfo.fromTopic(topic.getName(), subTopicName);
                });

        List<MyCalendarCourseInfo> combinedCourses = Stream.concat(sessionCourses, responseCourses)
                .limit(3)
                .toList();

        long totalCount = (long) sessions.size() + (long) responses.size();
        if (totalCount > 3) {
            List<MyCalendarCourseInfo> mutableList = new ArrayList<>(combinedCourses);
            mutableList.add(MyCalendarCourseInfo.fromExtra((int) (totalCount - 3)));
            return mutableList;
        }

        return combinedCourses;
    }

    public List<ReviewNoteItemResponse> getReviewNotes(User user, LocalDate date) {
        if (date == null) date = LocalDate.now();
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        Long userId = user.getId();

        List<UserAnswer> answers = userAnswerRepository.findUserAnswersByDateRange(userId, startOfDay, endOfDay);
        List<SharedResponse> responses = sharedResponseRepository.findByUserAndCreatedAtBetween(user, startOfDay, endOfDay);

        List<ReviewNoteItemResponse> reviewNotes = new ArrayList<>();

        for (UserAnswer answer : answers) {
            Content content = answer.getContent();
            StudiedStep studiedStep = answer.getStudiedStep();
            Step step = studiedStep.getStep();
            Topic topic = step.getSession().getCourse().getTopic();
            StepType stepType = step.getType();
            Level level = convertStepTypeToLevel(stepType);

            ContentResponse contentDto = null;
            AnswerResponse answerDto = null;

            try {
                switch (stepType) {
                    case OX_QUIZ:
                        contentDto = OxQuizContentResponse.fromEntity((OxQuiz) content);
                        answerDto = SimpleAnswerResponse.fromEntity((SimpleAnswer) answer);
                        break;
                    case MULTIPLE_CHOICE:
                        contentDto = MultipleChoiceContentResponse.fromEntity((MultipleChoiceQuiz) content);
                        answerDto = SimpleAnswerResponse.fromEntity((SimpleAnswer) answer);
                        break;
                    case SENTENCE_COMPLETION:
                        contentDto = SentenceCompletionContentResponse.fromEntity((SentenceCompletionQuiz) content);
                        answerDto = SentenceCompletionAnswerResponse.fromEntity((SentenceCompletionAnswer) answer);
                        break;
                    case SHORT_ANSWER:
                        contentDto = ShortAnswerContentResponse.fromEntity((ShortAnswerQuiz) content);
                        answerDto = SimpleAnswerResponse.fromEntity((SimpleAnswer) answer);
                        break;
                    case SUMMARY_READING:
                        contentDto = SummaryReadingContentResponse.fromEntity((SummaryReading) content);
                        answerDto = SummaryReadingAnswerResponse.fromEntity((SummaryReadingAnswer) answer);
                        break;
                    default: break;
                }
            } catch (ClassCastException e) { continue; }

            LocalDateTime createdAt = null;
            StudiedSession studiedSession = studiedSessionRepository.findByUserAndSession_Id(user, step.getSession().getId());

            if (studiedSession != null) {
                createdAt = studiedSession.getStartTime();
            } else {
                createdAt = startOfDay;
            }

            if (contentDto != null && answerDto != null) {
                reviewNotes.add(new ReviewNoteItemResponse(
                        topic.getName(), level, stepType, contentDto, answerDto, createdAt
                ));
            }
        }

        for (SharedResponse response : responses) {
            Step step = response.getStep();
            Topic topic = step.getSession().getCourse().getTopic();
            StepType stepType = step.getType();
            Level level = convertStepTypeToLevel(stepType);
            LocalDateTime createdAt = response.getCreatedAt();

            String reflectionQuestion = "질문을 불러올 수 없습니다.";
            try {
                List<Content> contents = contentRepository.findAllByStep(step);
                if (!contents.isEmpty() && contents.get(0) instanceof SessionReflection) {
                    reflectionQuestion = ((SessionReflection) contents.get(0)).getQuestion();
                }
            } catch (Exception e) { }

            reviewNotes.add(new ReviewNoteItemResponse(
                    topic.getName(), level, stepType, new SessionReflectionContentResponse(reflectionQuestion),
                    SharedReflectionResponse.fromEntity(response), createdAt
            ));
        }

        reviewNotes.sort(Comparator.comparing(ReviewNoteItemResponse::createdAt).reversed());
        return reviewNotes;
    }

    public MyTermListResponse getAllMyTerms(User user, String sort) {
        List<TermGroupRecord> groups = new ArrayList<>();
        if ("alphabetical".equals(sort)) {
            List<SavedTerm> savedTerms = savedTermRepository.findByUserOrderByTerm_NameAsc(user);
            List<TermItemRecord> termItems = savedTerms.stream().map(st -> new TermItemRecord(st.getTerm().getId(), st.getTerm().getName())).toList();
            Map<String, List<TermItemRecord>> groupedByInitial = termItems.stream().collect(Collectors.groupingBy(item -> getInitial(item.term()), LinkedHashMap::new, Collectors.toList()));
            groups = groupedByInitial.entrySet().stream().map(entry -> TermGroupRecord.forAlphabetical(entry.getKey(), entry.getValue())).toList();
        } else {
            List<SavedTerm> savedTerms = savedTermRepository.findByUserOrderBySavedAtDesc(user);
            LocalDate today = LocalDate.now();
            LocalDate yesterday = today.minusDays(1);
            LocalDate sevenDaysAgo = today.minusDays(7);
            List<TermItemRecord> todayTerms = new ArrayList<>(), yesterdayTerms = new ArrayList<>(), last7DaysTerms = new ArrayList<>(), otherTerms = new ArrayList<>();
            for (SavedTerm st : savedTerms) {
                if (st.getSavedAt() == null) continue;
                LocalDate savedDate = st.getSavedAt().toLocalDate();
                TermItemRecord item = new TermItemRecord(st.getTerm().getId(), st.getTerm().getName());
                if (savedDate.isEqual(today)) todayTerms.add(item);
                else if (savedDate.isEqual(yesterday)) yesterdayTerms.add(item);
                else if (savedDate.isAfter(sevenDaysAgo) && savedDate.isBefore(yesterday)) last7DaysTerms.add(item);
                else otherTerms.add(item);
            }
            if (!todayTerms.isEmpty()) groups.add(TermGroupRecord.forRecent("오늘", todayTerms));
            if (!yesterdayTerms.isEmpty()) groups.add(TermGroupRecord.forRecent("어제", yesterdayTerms));
            if (!last7DaysTerms.isEmpty()) groups.add(TermGroupRecord.forRecent("최근 7일", last7DaysTerms));
            if (!otherTerms.isEmpty()) groups.add(TermGroupRecord.forRecent("그 외", otherTerms));
        }
        return new MyTermListResponse(groups);
    }

    public TermContent getMyTermById(User user, Long termId) {
        Term term = termRepository.findById(termId).orElseThrow(() -> new EntityNotFoundException("Term not found with id: " + termId));
        if (!savedTermRepository.existsByUserAndTerm(user, term)) throw new EntityNotFoundException("사용자가 저장한 용어사전에서 해당 용어를 찾을 수 없습니다.");
        return TermContent.fromEntity(term);
    }

    private Level convertStepTypeToLevel(StepType type) {
        if (type == null) return Level.N;
        switch (type) {
            case OX_QUIZ: case TERM_LEARNING: case CURRENT_AFFAIRS: return Level.N;
            case MULTIPLE_CHOICE: case ARTICLE_READING: case SHORT_ANSWER: case SUMMARY_READING: return Level.I;
            case SESSION_REFLECTION: case SENTENCE_COMPLETION: return Level.E;
            default: return Level.N;
        }
    }

    private String getInitial(String text) {
        if (text == null || text.isEmpty()) return "?";
        char firstChar = text.charAt(0);
        if (firstChar >= '가' && firstChar <= '힣') {
            final char[] initials = {'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'};
            if (firstChar >= 'ㅅ' && firstChar < 'ㅇ') return "ㅅ";
            return String.valueOf(initials[(firstChar - '가') / (21 * 28)]);
        }
        if ((firstChar >= 'A' && firstChar <= 'Z') || (firstChar >= 'a' && firstChar <= 'z')) return String.valueOf(Character.toUpperCase(firstChar));
        return "#";
    }
}