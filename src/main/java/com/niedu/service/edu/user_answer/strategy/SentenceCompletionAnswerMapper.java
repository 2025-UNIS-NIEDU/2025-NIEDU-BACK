package com.niedu.service.edu.user_answer.strategy;

import com.niedu.dto.course.user_answer.AnswerResponse;
import com.niedu.entity.course.Step;
import com.niedu.entity.course.StepType;
import org.springframework.stereotype.Component;

@Component
public class SentenceCompletionAnswerMapper implements UserAnswerMapperStrategy{
    @Override
    public boolean supports(StepType type) {
        return false;
    }

    @Override
    public AnswerResponse toResponse(Step step) {
        return null;
    }
}
