package com.niedu.service.my;

import com.niedu.dto.my.*;
import com.niedu.dto.course.content.*;
import com.niedu.dto.course.user_answer.*;

import com.niedu.entity.content.*;
import com.niedu.entity.course.Level;
import com.niedu.entity.course.Session;
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

        // 1. 월간 학습 데이터를 한 번에 조회
        List<StudiedSession> sessions = studiedSessionRepository.findByUserAndStartTimeBetween(user, monthStart, monthEnd);
        List<SharedResponse> responses = sharedResponseRepository.findByUserAndCreatedAtBetween(user, monthStart, monthEnd);

        // 2. 날짜별로 데이터 그룹핑
        Map<LocalDate, List<StudiedSession>> sessionsByDate = sessions.stream()
                .collect(Collectors.groupingBy(s -> s.getStartTime().toLocalDate()));
        Map<LocalDate, List<SharedResponse>> responsesByDate = responses.stream()
                .collect(Collectors.groupingBy(r -> r.getCreatedAt().toLocalDate()));

        // 3. IntStream을 사용해 각 날짜별 DTO 생성
        List<MyCalendarResponse.DayDetail> days = IntStream.rangeClosed(1, daysInMonth)
                .mapToObj(day -> {
                    LocalDate currentDate = firstDayOfMonth.withDayOfMonth(day);
                    List<StudiedSession> sessionsToday = sessionsByDate.getOrDefault(currentDate, Collections.emptyList());
                    List<SharedResponse> responsesToday = responsesByDate.getOrDefault(currentDate, Collections.emptyList());

                    // 4. 헬퍼 메서드로 코스 정보 추출
                    List<MyCalendarCourseInfo> coursesForThisDay = extractCoursesForCalendar(sessionsToday, responsesToday);
                    return new MyCalendarResponse.DayDetail(currentDate.atStartOfDay(), coursesForThisDay);
                })
                .toList();

        return new MyCalendarResponse(year, month, days);
    }


    private List<MyCalendarCourseInfo> extractCoursesForCalendar(List<StudiedSession> sessions, List<SharedResponse> responses) {
        // 1. 두 리스트를 하나의 스트림으로 병합
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

        // 2. 병합된 스트림에서 명세서 기준 3개만 DTO로 변환
        List<MyCalendarCourseInfo> combinedCourses = Stream.concat(sessionCourses, responseCourses)
                .limit(3) // 3개까지 노출
                .toList();

        // 3. +n 계산 (명세서 SET-ALL-03)
        long totalCount = (long) sessions.size() + (long) responses.size();
        if (totalCount > 3) {
            List<MyCalendarCourseInfo> mutableList = new ArrayList<>(combinedCourses);
            mutableList.add(MyCalendarCourseInfo.fromExtra((int) (totalCount - 3)));
            return mutableList;
        }

        return combinedCourses;
    }


    public List<ReviewNoteItemResponse> getReviewNotes(User user, LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        Long userId = user.getId();

        // 1. 틀린 퀴즈 목록 조회
        List<UserAnswer> answers = userAnswerRepository
                .findUserAnswersByDateRange(userId, startOfDay, endOfDay);
        // 2. 세션 돌아보기 목록 조회
        List<SharedResponse> responses = sharedResponseRepository
                .findByUserAndCreatedAtBetween(user, startOfDay, endOfDay);

        List<ReviewNoteItemResponse> reviewNotes = new ArrayList<>();

        // 3-1. 틀린 퀴즈 목록 DTO로 변환
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
                    default:
                        log.warn("getReviewNotes: 처리되지 않은 퀴즈 contentType [{}]", stepType);
                        break;
                }
            } catch (ClassCastException e) {
                log.error("Content/Answer (ID: {})와 StepType ({})이 일치하지 않습니다.", content.getId(), stepType, e);
                continue;
            }


            LocalDateTime createdAt = null;
            StudiedSession studiedSession = studiedSessionRepository
                    .findByUserAndSession_Id(user, step.getSession().getId()); // [FIX]
            if (studiedSession != null) {
                createdAt = studiedSession.getStartTime();
            }


            if (contentDto != null && answerDto != null && createdAt != null) {
                reviewNotes.add(new ReviewNoteItemResponse(
                        topic.getName(),
                        level,
                        stepType,
                        contentDto,
                        answerDto,
                        createdAt
                ));
            } else if (createdAt == null) {
                log.warn("StudiedSession을 찾을 수 없어 createdAt이 null입니다. User: {}, Session: {}", user.getId(), step.getSession().getId());
            }
        }

        // 3-2. 세션  목록 DTO로 변환
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
            } catch (Exception e) {
                log.error("SESSION_REFLECTION 질문을 찾는 중 오류 발생. Step ID: {}", step.getId(), e);
            }

            ContentResponse contentDto = new SessionReflectionContentResponse(reflectionQuestion);
            AnswerResponse answerDto = SharedReflectionResponse.fromEntity(response);

            reviewNotes.add(new ReviewNoteItemResponse(
                    topic.getName(),
                    level,
                    stepType,
                    contentDto,
                    answerDto,
                    createdAt
            ));
        }

        // 4. 최종 정렬
        reviewNotes.sort(Comparator.comparing(ReviewNoteItemResponse::createdAt).reversed());

        return reviewNotes;
    }


    public MyTermListResponse getAllMyTerms(User user, String sort) {
        List<TermGroupRecord> groups = new ArrayList<>();

        if ("alphabetical".equals(sort)) {
            List<SavedTerm> savedTerms = savedTermRepository.findByUserOrderByTerm_NameAsc(user);

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

        } else {
            List<SavedTerm> savedTerms = savedTermRepository.findByUserOrderBySavedAtDesc(user);

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


    public TermContent getMyTermById(User user, Long termId) {
        Term term = termRepository.findById(termId)
                .orElseThrow(() -> new EntityNotFoundException("Term not found with id: " + termId));

        if (!savedTermRepository.existsByUserAndTerm(user, term)) {
            throw new EntityNotFoundException("사용자가 저장한 용어사전에서 해당 용어를 찾을 수 없습니다.");
        }
        return TermContent.fromEntity(term);
    }


    private Level convertStepTypeToLevel(StepType type) {
        if (type == null) {
            return Level.N;
        }

        switch (type) {
            case OX_QUIZ:
            case TERM_LEARNING:
            case CURRENT_AFFAIRS:
                return Level.N;

            case MULTIPLE_CHOICE:
            case ARTICLE_READING:
            case SHORT_ANSWER:
            case SUMMARY_READING:
                return Level.I;

            case SESSION_REFLECTION:
            case SENTENCE_COMPLETION:
                return Level.E;

            default:
                return Level.N;
        }
    }


    private String getInitial(String text) {
        if (text == null || text.isEmpty()) {
            return "?";
        }
        char firstChar = text.charAt(0);


        if (firstChar >= '가' && firstChar <= '힣') {
            final char[] initials = {
                    'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ',
                    'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
            };

            if (firstChar >= 'ㅅ' && firstChar < 'ㅇ') return "ㅅ";

            int index = (firstChar - '가') / (21 * 28);
            return String.valueOf(initials[index]);
        }


        if ((firstChar >= 'A' && firstChar <= 'Z') || (firstChar >= 'a' && firstChar <= 'z')) {
            return String.valueOf(Character.toUpperCase(firstChar));
        }

        return "#";
    }
}