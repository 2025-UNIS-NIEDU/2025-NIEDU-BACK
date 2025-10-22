package com.niedu.dto.course;

import com.niedu.dto.course.content.ContentResponse;
import com.niedu.dto.course.user_answer.AnswerResponse;
import com.niedu.entity.course.StepType;

public record StepListResponse (
        Long stepId,
        Integer stepOrder,
        Boolean isCompleted,
        StepType contentType,
        ContentResponse contentResponse,
        AnswerResponse userAnswerResponse
) {}