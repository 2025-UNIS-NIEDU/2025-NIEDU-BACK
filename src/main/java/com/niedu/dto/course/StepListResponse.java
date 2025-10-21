package com.niedu.dto.course;

import com.niedu.dto.course.content.ContentResponse;
import com.niedu.dto.course.user_answer.UserAnswerResponse;
import com.niedu.entity.course.StepType;

public record StepListResponse (
        Integer stepId,
        Integer stepOrder,
        Boolean isCompleted,
        StepType contentType,
        ContentResponse contentResponse,
        UserAnswerResponse userAnswerResponse
) {}