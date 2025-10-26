package com.niedu.service.edu.content.strategy;

import com.niedu.dto.course.content.ContentResponse;
import com.niedu.dto.course.content.ShortAnswerContentListResponse;
import com.niedu.dto.course.content.ShortAnswerContentResponse;
import com.niedu.entity.content.Content;
import com.niedu.entity.content.ShortAnswerQuiz;
import com.niedu.entity.course.Step;
import com.niedu.entity.course.StepType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class ShortAnswerContentMapper implements ContentMapperStrategy{
    @Override
    public boolean supports(StepType type) {
        if (!type.equals(StepType.SHORT_ANSWER)) return false;
        return true;
    }

    @Override
    public ContentResponse toResponse(Step step, List<Content> contents) {
        List<ShortAnswerQuiz> shortAnswerQuizs = contents.stream()
                .filter(content -> content instanceof ShortAnswerQuiz)
                .map(content -> (ShortAnswerQuiz) content)
                .toList();
        if (shortAnswerQuizs == null || shortAnswerQuizs.isEmpty()) throw new RuntimeException("content 조회 실패");

        List<ShortAnswerContentResponse> shortAnswerContentResponses = shortAnswerQuizs.stream()
                .map(shortAnswerQuiz -> new ShortAnswerContentResponse(
                        shortAnswerQuiz.getId(),
                        shortAnswerQuiz.getQuestion(),
                        shortAnswerQuiz.getCorrectAnswer(),
                        shortAnswerQuiz.getAnswerExplanation()
                ))
                .toList();
        return new ShortAnswerContentListResponse(
                step.getSession().getNewsRef().getSourceUrl(),
                shortAnswerContentResponses
        );
    }
}
