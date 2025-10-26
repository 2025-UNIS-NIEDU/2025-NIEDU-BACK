package com.niedu.service.edu.content.strategy;

import com.niedu.dto.course.content.ContentResponse;
import com.niedu.dto.course.content.SentenceCompletionContentListResponse;
import com.niedu.dto.course.content.SentenceCompletionContentResponse;
import com.niedu.entity.content.Content;
import com.niedu.entity.content.SentenceCompletionQuiz;
import com.niedu.entity.course.Step;
import com.niedu.entity.course.StepType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

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
}
