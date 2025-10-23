package com.niedu.service.edu.content.strategy;

import com.niedu.dto.course.content.ContentResponse;
import com.niedu.dto.course.content.SentenceCompletionContentResponse;
import com.niedu.entity.content.Content;
import com.niedu.entity.content.SentenceCompletionQuiz;
import com.niedu.entity.course.Step;
import com.niedu.entity.course.StepType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SentenceCompletionContentMapper implements ContentMapperStrategy {
    @Override
    public boolean supports(StepType type) {
        if (!type.equals(StepType.SENTENCE_COMPLETION)) return false;
        return true;
    }

    @Override
    public ContentResponse toResponse(Step step) {
        Content content = step.getContent();
        if (content instanceof SentenceCompletionQuiz sentenceCompletionQuiz) {
            return new SentenceCompletionContentResponse(
                    sentenceCompletionQuiz.getQuestion()
            );
        }
        else {
            log.warn("이 Step은 SentenceCompletionQuiz 타입이 아닙니다: {}", content.getClass().getSimpleName());
            return null;

        }
    }
}
