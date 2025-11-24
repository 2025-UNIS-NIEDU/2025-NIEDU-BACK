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
import java.util.Map;

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
                .map(raw -> (Map<String, Object>) raw)
                .map(map -> {
                    List<Map<String, Object>> options = (List<Map<String, Object>>) map.get("options");

                    return MultipleChoiceQuiz.builder()
                            .step(step)
                            .question((String) map.get("question"))
                            .answerExplanation((String) map.get("answerExplanation"))
                            .optionA((String) options.get(0).get("text"))
                            .optionB((String) options.get(1).get("text"))
                            .optionC((String) options.get(2).get("text"))
                            .optionD((String) options.get(3).get("text"))
                            .correctAnswer((String) map.get("correctAnswer"))   // ★ 추가
                            .build();
                })
                .map(q -> (Content) q)
                .toList();
    }
}
