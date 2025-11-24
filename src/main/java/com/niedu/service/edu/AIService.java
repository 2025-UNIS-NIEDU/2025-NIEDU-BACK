package com.niedu.service.edu;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.niedu.dto.course.FeedbackAnswerRequest;
import com.niedu.dto.course.FeedbackAnswerResponse;
import com.niedu.dto.course.ai.*;
import com.niedu.entity.content.Content;
import com.niedu.entity.content.NewsRef;
import com.niedu.entity.course.*;
import com.niedu.entity.topic.SubTopic;
import com.niedu.entity.topic.Topic;
import com.niedu.entity.user.User;
import com.niedu.repository.content.ContentRepository;
import com.niedu.repository.content.NewsRefRepository;
import com.niedu.repository.content.TermRepository;
import com.niedu.repository.course.*;
import com.niedu.repository.topic.SubTopicRepository;
import com.niedu.repository.topic.TopicRepository;
import com.niedu.service.edu.content.StepMapperService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AIService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private final CourseRepository courseRepository;
    private final TopicRepository topicRepository;
    private final SubTopicRepository subTopicRepository;
    private final CourseSubTagRepository courseSubTagRepository;
    private final CourseSubTopicRepository courseSubTopicRepository;

    private final NewsRefRepository newsRefRepository;
    private final SessionRepository sessionRepository;

    private final StepRepository stepRepository;
    private final StepMapperService stepMapperService;
    private final ContentRepository contentRepository;

    private final TermRepository termRepository;

    @Value("${external.ai-server.url}")
    private String aiServerUrl;

    @Value("${external.ai-server.api-key}")
    private String aiServerApiKey;

    public FeedbackAnswerResponse submitStepAnswerForFeedback(User user, Long stepId, FeedbackAnswerRequest request) {
        String url = aiServerUrl + "/api/feedback";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("X-API-KEY", aiServerApiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(objectMapper.convertValue(request, new TypeReference<>(){}), headers);
        ResponseEntity<FeedbackAnswerResponse> response = null;
        try {
            response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    FeedbackAnswerResponse.class
            );

        } catch (HttpClientErrorException e) {
            log.error("클라이언트 오류: " + e.getStatusCode());
            log.error("응답 바디: " + e.getResponseBodyAsString());
        } catch (HttpServerErrorException e) {
            log.error("서버 오류: " + e.getStatusCode());
        } catch (ResourceAccessException e) {
            log.error("연결 실패: " + e.getMessage());
        }

        return response != null ? response.getBody() : null;
    }

    public void syncAIData() {
        String url = aiServerUrl + "/api/course/today";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("X-API-KEY", aiServerApiKey);

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = null;
        try {
            response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );
        } catch (HttpClientErrorException e) {
            log.error("클라이언트 오류: {} - 응답 바디: {}", e.getStatusCode(), e.getResponseBodyAsString());
            return;
        } catch (HttpServerErrorException e) {
            log.error("서버 오류: {}", e.getStatusCode());
            return;
        } catch (ResourceAccessException e) {
            log.error("연결 실패: {}", e.getMessage());
            return;
        }

        // 디버깅: RestTemplate 응답 상태 확인
        if (response != null) {
            log.info("AI Server Status Code: {}", response.getStatusCode());
            log.info("AI Server Response Headers: {}", response.getHeaders());
        } else {
            log.error("AI Server 응답 객체(ResponseEntity) 자체가 null입니다. RestTemplate 통신 문제 의심.");
        }

        String content = response != null ? response.getBody() : null;

        if (content == null || content.isEmpty() || content.trim().length() < 3) {
            log.warn("AI 서버로부터 받은 응답 본문이 비어 있거나 유효하지 않아 데이터 적재를 건너뜁니다.");
            return;
        }

        try {
            AICourseListResponse aiCourseListResponse = objectMapper.readValue(content, AICourseListResponse.class);
            importCourses(aiCourseListResponse.courses());

        } catch (JsonProcessingException e) {
            log.error("JSON 파싱 오류 발생: {}", e.getMessage());
            // JSON 파싱 오류 시에도 스케줄러가 멈추지 않고 안전하게 종료됩니다.
        }
    }

    // 저장용 메소드
    @Transactional
    public void importCourses(List<AICourseResponse> courses) {
        // AI 서버 응답 DTO에서 courses 리스트가 null로 파싱되는 경우를 대비하여 방어 코드를 추가합니다.
        if (courses == null || courses.isEmpty()) {
            log.warn("Received null or empty course list from AI server. Skipping course import.");
            return;
        }
        courses.forEach(this::saveCourseWithRelations);
    }

    private void saveCourseWithRelations(AICourseResponse courseResponse) {
        // 1. Topic/SubTopic 존재 확인 or 생성
        Topic topic = topicRepository.findByName(courseResponse.topic());
        SubTopic subTopic = subTopicRepository.findByName(courseResponse.subTopic());

        // 2. Course 생성 및 저장
        Course course = courseRepository.save(Course.builder()
                .title(courseResponse.courseName())
                .description(courseResponse.courseDescription())
                .thumbnailUrl(courseResponse.sessions().get(0).thumbnailUrl())
                .createdAt(LocalDateTime.now())
                .topic(topic)
                .build());

        // 3. SubTags 저장
        List<CourseSubTag> tags = courseResponse.subTags().stream()
                .map(tag -> CourseSubTag.builder()
                        .course(course)
                        .tag(tag)
                        .build())
                .toList();
        courseSubTagRepository.saveAll(tags);

        // 4. SubTopic 매핑 저장
        courseSubTopicRepository.save(new CourseSubTopic(course, subTopic));

        // 5. Sessions 저장
        courseResponse.sessions().forEach(sessionResponse ->
                saveSessionWithChildren(course, sessionResponse)
        );
    }

    private void saveSessionWithChildren(Course course, AISessionResponse sessionResponse) {

        // 1. NewsRef 저장
        NewsRef newsRef = newsRefRepository.save(NewsRef.builder()
                .headline(sessionResponse.headline())
                .publisher(sessionResponse.publisher())
                .publishedAt(sessionResponse.publishedAt().toLocalDate())
                .thumbnailUrl(sessionResponse.thumbnailUrl())
                .sourceUrl(sessionResponse.sourceUrl())
                .build());

        // 2. Session 저장
        Session session = sessionRepository.save(Session.builder()
                .course(course)
                .newsRef(newsRef)
                .build());

        // 3. NPE-safe quizzes
        List<AIQuizResponse> quizzes = sessionResponse.quizzes();
        if (quizzes == null || quizzes.isEmpty()) {
            log.warn("Session({}) has NO quizzes. Skipping...", session.getId());
            return;
        }

        for (AIQuizResponse quiz : quizzes) {

            List<AIStepResponse> steps = quiz.steps();
            if (steps == null || steps.isEmpty()) {
                log.warn("Quiz in session({}) has NO steps. Skipping...", session.getId());
                continue;
            }

            for (AIStepResponse stepResponse : steps) {
                saveStepWithContents(session, stepResponse);
            }
        }
    }

    private void saveStepWithContents(Session session, AIStepResponse stepResponse) {
        Step step = stepRepository.save(Step.builder()
                .session(session)
                .stepOrder(stepResponse.stepOrder())
                .type(stepResponse.contentType())
                .build());

        // normalize 적용
        List<Object> rawContents = stepResponse.contents();
        if (rawContents == null) rawContents = List.of();

        List<Object> normalizedContents = rawContents.stream()
                .map(this::normalize)
                .filter(Objects::nonNull)
                .map(c -> (Object) c)
                .toList();

        // stepResponse의 contents를 정상화된 값으로 교체
        AIStepResponse safeStep = new AIStepResponse(
                stepResponse.stepOrder(),
                stepResponse.contentType(),
                normalizedContents
        );

        List<Content> contents = stepMapperService.toEntities(step, safeStep);

        if (contents != null)
            contentRepository.saveAll(contents);
    }

    private Map<String, Object> normalize(Object obj) {

        // 1) Map 이면 바로 변환
        if (obj instanceof Map<?, ?> raw) {
            return raw.entrySet().stream()
                    .collect(Collectors.toMap(
                            e -> String.valueOf(e.getKey()),
                            Map.Entry::getValue
                    ));
        }

        // 2) List 이면 내부 Map 찾아서 변환
        if (obj instanceof List<?> list) {
            return list.stream()
                    .filter(item -> item instanceof Map<?, ?>)
                    .findFirst()
                    .map(this::normalize)
                    .orElse(null);
        }

        // 3) 그 외 타입은 무시
        return null;
    }
}