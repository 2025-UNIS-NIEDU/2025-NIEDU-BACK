package com.niedu.service.edu;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.niedu.dto.course.FeedbackAnswerRequest;
import com.niedu.dto.course.FeedbackAnswerResponse;
import com.niedu.dto.course.ai.AICourseListResponse;
import com.niedu.dto.course.ai.AICourseResponse;
import com.niedu.dto.course.ai.AISessionResponse;
import com.niedu.dto.course.ai.AIStepResponse;
import com.niedu.dto.course.content.ContentResponse;
import com.niedu.dto.course.content.TermContent;
import com.niedu.dto.course.content.TermLearningContentResponse;
import com.niedu.entity.content.Content;
import com.niedu.entity.content.NewsRef;
import com.niedu.entity.content.Term;
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

        return response.getBody();
    }

    public void syncAIData() {
        String url = aiServerUrl + "/api/build";

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
            log.error("클라이언트 오류: " + e.getStatusCode());
            log.error("응답 바디: " + e.getResponseBodyAsString());
        } catch (HttpServerErrorException e) {
            log.error("서버 오류: " + e.getStatusCode());
        } catch (ResourceAccessException e) {
            log.error("연결 실패: " + e.getMessage());
        }

        try {
            AICourseListResponse aiCourseListResponse = objectMapper.readValue(response.getBody(), AICourseListResponse.class);
            importCourses(aiCourseListResponse.courses());

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    // 저장용 메소드
    @Transactional
    public void importCourses(List<AICourseResponse> courses) {
        courses.forEach(this::saveCourseWithRelations);
    }

    private void saveCourseWithRelations(AICourseResponse courseResponse) {
        // 1. Topic/SubTopic 존재 확인 or 생성
        Topic topic = topicRepository.findByName(courseResponse.topic());
        SubTopic subTopic = subTopicRepository.findByName(courseResponse.subtopic());

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
        // 1. NewsRef 생성
        NewsRef newsRef = newsRefRepository.save(NewsRef.builder()
                .headline(sessionResponse.headline())
                .publisher(sessionResponse.publisher())
                .publishedAt(sessionResponse.publishedAt().toLocalDate())
                .thumbnailUrl(sessionResponse.thumbnailUrl())
                .sourceUrl(sessionResponse.sourceUrl())
                .build());

        // 2. Session 생성
        Session session = sessionRepository.save(Session.builder()
                .course(course)
                .newsRef(newsRef)
                .build());

        // 3. Steps 저장
        sessionResponse.quizzes().forEach(quizResponse -> {
            quizResponse.steps().forEach(stepResponse -> {
                saveStepWithContents(session, stepResponse);
            });
        });

        // 4. TermLearning 단계일 경우 Terms 저장
        sessionResponse.quizzes().stream()
                .flatMap(q -> q.steps().stream())
                .filter(s -> s.contentType().equals(StepType.TERM_LEARNING))
                .forEach(stepResponse ->
                        saveTerms(session, stepResponse)
                );
    }

    private void saveStepWithContents(Session session, AIStepResponse stepResponse) {
        Step step = stepRepository.save(Step.builder()
                .session(session)
                .stepOrder(stepResponse.stepOrder())
                .type(stepResponse.contentType())
                .build());

        List<Content> contents = stepMapperService.toEntities(step, stepResponse);
        if (contents != null)
            contentRepository.saveAll(contents);
    }

    private void saveTerms(Session session, AIStepResponse stepResponse) {
        List<ContentResponse> contents = stepResponse.contents();
        contents.stream()
                .filter(contentResponse -> contentResponse instanceof TermLearningContentResponse)
                .map(contentResponse -> (TermLearningContentResponse) contentResponse)
                .forEach(termLearningContentResponse -> {
                    termLearningContentResponse.terms().forEach(termContent -> {
                        termRepository.save(Term.builder()
                                .name(termContent.name())
                                .session(session)
                                .definition(termContent.definition())
                                .exampleSentence(termContent.exampleSentence())
                                .additionalExplanation(termContent.additionalExplanation())
                                .build());
                    });
                });
    }
}
