package com.niedu.dto.course;

import com.niedu.dto.course.user_answer.UserAnswerResponse;
import com.niedu.entity.course.StepType;

public record StepAnswerRequest (
        StepType contentType,
        UserAnswerResponse userAnswer
) {}