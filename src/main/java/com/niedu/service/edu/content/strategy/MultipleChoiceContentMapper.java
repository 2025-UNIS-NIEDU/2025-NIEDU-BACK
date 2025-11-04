package com.niedu.service.edu.content.strategy;

import com.niedu.dto.course.ai.AIStepResponse;
import com.niedu.dto.course.content.ContentResponse;
import com.niedu.dto.course.content.MultipleChoiceContentListResponse;
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
    public ContentResponse toResponse(Step step, List<Content> contents) {
        List<MultipleChoiceQuiz> multipleChoiceQuizs = contents.stream()
                .filter(content -> content instanceof MultipleChoiceQuiz)
                .map(content -> (MultipleChoiceQuiz) content)
                .toList();
        if (multipleChoiceQuizs == null || multipleChoiceQuizs.isEmpty()) throw new RuntimeException("content 조회 실패");

        List<MultipleChoiceContentResponse> multipleChoiceContentResponses = multipleChoiceQuizs.stream()
                .map(multipleChoiceQuiz -> {
                    List<MultipleChoiceOptionContent> options = new ArrayList<>(List.of(
                            new MultipleChoiceOptionContent("A", multipleChoiceQuiz.getOptionA()),
                            new MultipleChoiceOptionContent("B", multipleChoiceQuiz.getOptionB()),
                            new MultipleChoiceOptionContent("C", multipleChoiceQuiz.getOptionC()),
                            new MultipleChoiceOptionContent("D", multipleChoiceQuiz.getOptionD())
                    ));
                    return new MultipleChoiceContentResponse(
                            multipleChoiceQuiz.getId(),
                            multipleChoiceQuiz.getQuestion(),
                            options,
                            multipleChoiceQuiz.getCorrectAnswer(),
                            multipleChoiceQuiz.getAnswerExplanation()
                    );
                })
                .toList();
        return new MultipleChoiceContentListResponse(
                step.getSession().getNewsRef().getSourceUrl(),
                multipleChoiceContentResponses
        );
    }

    @Override
    public List<Content> toEntities(Step step, AIStepResponse stepResponse) {
        return stepResponse.contents().stream()
                .filter(c -> c instanceof MultipleChoiceContentResponse)
                .map(c -> (MultipleChoiceContentResponse) c)
                .map(content -> MultipleChoiceQuiz.builder()
                        .step(step)
                        .question(content.question())
                        .answerExplanation(content.answerExplanation())
                        .optionA(content.options().get(0).text())
                        .optionB(content.options().get(1).text())
                        .optionC(content.options().get(2).text())
                        .optionD(content.options().get(3).text())
                        .build())
                .map(multipleChoiceQuiz -> (Content) multipleChoiceQuiz)
                .toList();
    }
}
