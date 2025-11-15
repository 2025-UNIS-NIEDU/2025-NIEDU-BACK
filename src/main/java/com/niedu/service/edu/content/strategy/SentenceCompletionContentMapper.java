package com.niedu.service.edu.content.strategy;

import com.niedu.dto.course.ai.AIStepResponse;
import com.niedu.dto.course.content.ContentResponse;
import com.niedu.dto.course.content.SentenceCompletionContentListResponse;
import com.niedu.dto.course.content.SentenceCompletionContentResponse;
import com.niedu.entity.content.Content;
import com.niedu.entity.content.SentenceCompletionQuiz;
import com.niedu.entity.course.Step;
import com.niedu.entity.course.StepType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.JpaSort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Slf4j
@Component
public class SentenceCompletionContentMapper implements ContentMapperStrategy {
    @Override
    public boolean supports(StepType type) {
        if (!type.equals(StepType.SENTENCE_COMPLETION)) return false;
        return true;
    }

    @Override
    public ContentResponse toResponse(Step step, List<Content> contents) {
        List<SentenceCompletionQuiz> sentenceCompletionQuizs = contents.stream()
                .filter(content -> content instanceof SentenceCompletionQuiz)
                .map(content -> (SentenceCompletionQuiz) content)
                .toList();
        if (sentenceCompletionQuizs == null || sentenceCompletionQuizs.isEmpty())
            throw new RuntimeException("content 조회 실패");

        List<SentenceCompletionContentResponse> sentenceCompletionContentResponses = sentenceCompletionQuizs.stream()
                .map(sentenceCompletionQuiz -> new SentenceCompletionContentResponse(
                        sentenceCompletionQuiz.getId(),
                        sentenceCompletionQuiz.getQuestion()
                ))
                .toList();
        return new SentenceCompletionContentListResponse(sentenceCompletionContentResponses);
    }

    @Override
    public List<Content> toEntities(Step step, AIStepResponse stepResponse) {

        return stepResponse.contents().stream()
                .flatMap(raw -> {

                    // 1) raw = Map<String, Object>
                    if (raw instanceof Map<?, ?> map) {
                        return Stream.of(createEntity(step, (Map<String, Object>) map));
                    }

                    // 2) raw = List (e.g., [ {..}, {..} ])
                    if (raw instanceof List<?> list) {
                        return list.stream()
                                .filter(item -> item instanceof Map<?, ?>)
                                .map(item -> createEntity(step, (Map<String, Object>) item))
                                .flatMap(Stream::of);
                    }

                    // 3) 그 외 형태는 무시
                    log.warn("Unknown SENTENCE_COMPLETION content structure: {}", raw);
                    return Stream.<Content>empty();
                })
                .map(c -> (Content) c)
                .toList();
    }

    private SentenceCompletionQuiz createEntity(Step step, Map<String, Object> map) {
        return SentenceCompletionQuiz.builder()
                .step(step)
                .question((String) map.get("question"))
                .referenceAnswer((String) map.get("referenceAnswer"))
                .build();
    }
}
