package com.niedu.service.edu.content.strategy;

import com.niedu.dto.course.content.ContentResponse;
import com.niedu.dto.course.content.MultipleChoiceContentResponse;
import com.niedu.dto.course.content.MultipleChoiceOptionContent;
import com.niedu.entity.content.Content;
import com.niedu.entity.content.MultipleChoiceQuiz;
import com.niedu.entity.course.Step;
import com.niedu.entity.course.StepType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class MultipleChoiceContentMapper implements ContentMapperStrategy{
    @Override
    public boolean supports(StepType type) {
        if (!type.equals(StepType.MULTIPLE_CHOICE)) return false;
        return true;
    }

    @Override
    public ContentResponse toResponse(Step step) {
        Content content = step.getContent();
        if (content instanceof MultipleChoiceQuiz multipleChoiceQuiz) {
            ArrayList<MultipleChoiceOptionContent> options = new ArrayList<>(List.of(
                    new MultipleChoiceOptionContent("A", multipleChoiceQuiz.getOptionA()),
                    new MultipleChoiceOptionContent("B", multipleChoiceQuiz.getOptionB()),
                    new MultipleChoiceOptionContent("C", multipleChoiceQuiz.getOptionC()),
                    new MultipleChoiceOptionContent("D", multipleChoiceQuiz.getOptionD())
            ));
            return new MultipleChoiceContentResponse(
                    multipleChoiceQuiz.getQuestion(),
                    options,
                    multipleChoiceQuiz.getCorrectAnswer(),
                    multipleChoiceQuiz.getAnswerExplanation(),
                    step.getSession().getNewsRef().getSourceUrl()
            );
        }
        else {
            log.warn("이 Step은 MultipleChoiceQuiz 타입이 아닙니다: {}", content.getClass().getSimpleName());
            return null;
        }
    }
}
