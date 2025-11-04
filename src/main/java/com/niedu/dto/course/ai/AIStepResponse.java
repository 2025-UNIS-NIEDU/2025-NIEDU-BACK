package com.niedu.dto.course.ai;

import com.niedu.dto.course.content.ContentResponse;
import com.niedu.entity.course.StepType;

import java.util.List;

public record AIStepResponse(
        Integer stepOrder,
        StepType contentType,
        List<ContentResponse> contents
) {
}
