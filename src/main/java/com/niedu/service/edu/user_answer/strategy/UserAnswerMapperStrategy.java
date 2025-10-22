package com.niedu.service.edu.user_answer.strategy;

import com.niedu.dto.course.user_answer.AnswerResponse;
import com.niedu.entity.course.Step;
import com.niedu.entity.course.StepType;

public interface UserAnswerMapperStrategy {
    boolean supports(StepType type);
    AnswerResponse toResponse(Step step);
}
