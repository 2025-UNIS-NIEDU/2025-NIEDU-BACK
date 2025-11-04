package com.niedu.dto.course.ai;

import com.niedu.entity.course.Level;

import java.util.List;

public record AIQuizResponse(
        Level level,
        List<AIStepResponse> steps
) {
}
