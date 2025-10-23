package com.niedu.service.edu.content.strategy;

import com.niedu.dto.course.content.ContentResponse;
import com.niedu.dto.course.content.ShortAnswerContentResponse;
import com.niedu.entity.content.Content;
import com.niedu.entity.content.ShortAnswerQuiz;
import com.niedu.entity.course.Step;
import com.niedu.entity.course.StepType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ShortAnswerContentMapper implements ContentMapperStrategy{
    @Override
    public boolean supports(StepType type) {
        if (!type.equals(StepType.SHORT_ANSWER)) return false;
        return true;
    }

    @Override
    public ContentResponse toResponse(Step step) {
        Content content = step.getContent();
        if (content instanceof ShortAnswerQuiz shortAnswerQuiz) {
            return new ShortAnswerContentResponse(
                    shortAnswerQuiz.getQuestion(),
                    shortAnswerQuiz.getCorrectAnswer(),
                    shortAnswerQuiz.getAnswerExplanation(),
                    step.getSession().getNewsRef().getSourceUrl()
            );
        }
        else {
            log.warn("이 Step은 ShortAnswer 타입이 아닙니다: {}", content.getClass().getSimpleName());
            return null;
        }
    }
}
