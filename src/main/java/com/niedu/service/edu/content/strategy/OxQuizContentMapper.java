package com.niedu.service.edu.content.strategy;

import com.niedu.dto.course.ai.AIStepResponse;
import com.niedu.dto.course.content.ContentResponse;
import com.niedu.dto.course.content.OxQuizContentListResponse;
import com.niedu.dto.course.content.OxQuizContentResponse;
import com.niedu.entity.content.Content;
import com.niedu.entity.content.OxQuiz;
import com.niedu.entity.course.Step;
import com.niedu.entity.course.StepType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class OxQuizContentMapper implements ContentMapperStrategy{
    @Override
    public boolean supports(StepType type) {
        if (!type.equals(StepType.OX_QUIZ)) return false;
        return true;
    }

    @Override
    public ContentResponse toResponse(Step step, List<Content> contents) {
        List<OxQuiz> oxQuizs = contents.stream()
                .filter(content -> content instanceof OxQuiz)
                .map(content -> (OxQuiz) content)
                .toList();
        if (oxQuizs == null || oxQuizs.isEmpty()) throw new RuntimeException("content 조회 실패");

        List<OxQuizContentResponse> oxQuizContentResponses = oxQuizs.stream()
                .map(oxQuiz -> new OxQuizContentResponse(
                        oxQuiz.getId(),
                        oxQuiz.getQuestion(),
                        oxQuiz.getCorrectAnswer(),
                        oxQuiz.getAnswerExplanation()
                ))
                .toList();
        return new OxQuizContentListResponse(
                step.getSession().getNewsRef().getSourceUrl(),
                oxQuizContentResponses
        );
    }

    @Override
    public List<Content> toEntities(Step step, AIStepResponse stepResponse) {
        return stepResponse.contents().stream()
                .filter(c -> c instanceof OxQuizContentResponse)
                .map(c -> (OxQuizContentResponse) c)
                .map(content -> OxQuiz.builder()
                        .step(step)
                        .question(content.question())
                        .correctAnswer(content.correctAnswer())
                        .answerExplanation(content.answerExplanation())
                        .build())
                .map(oxQuiz -> (Content) oxQuiz)
                .toList();
    }
}
