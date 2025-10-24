package com.niedu.service.edu.content.strategy;

import com.niedu.dto.course.content.ContentResponse;
import com.niedu.dto.course.content.OxQuizContentResponse;
import com.niedu.entity.content.Content;
import com.niedu.entity.content.OxQuiz;
import com.niedu.entity.course.Step;
import com.niedu.entity.course.StepType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OxQuizContentMapper implements ContentMapperStrategy{
    @Override
    public boolean supports(StepType type) {
        if (!type.equals(StepType.OX_QUIZ)) return false;
        return true;
    }

    @Override
    public ContentResponse toResponse(Step step) {
        Content content = step.getContent();
        if (content instanceof OxQuiz oxQuiz) {
            return new OxQuizContentResponse(
                    oxQuiz.getQuestion(),
                    oxQuiz.getCorrectAnswer(),
                    oxQuiz.getAnswerExplanation(),
                    step.getSession().getNewsRef().getSourceUrl()
            );
        }
        else {
            log.warn("이 Step은 OxQuiz 타입이 아닙니다: {}", content.getClass().getSimpleName());
            return null;
        }
    }
}
