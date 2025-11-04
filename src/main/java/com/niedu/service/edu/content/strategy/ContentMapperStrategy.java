package com.niedu.service.edu.content.strategy;

import com.niedu.dto.course.ai.AIStepResponse;
import com.niedu.dto.course.content.ContentResponse;
import com.niedu.entity.content.Content;
import com.niedu.entity.course.Step;
import com.niedu.entity.course.StepType;

import java.util.List;

// Step 엔티티 → ContentResponse DTO 변환을 위한 전략 인터페이스
public interface ContentMapperStrategy {
    boolean supports(StepType type);
    ContentResponse toResponse(Step step, List<Content> contents);

    default List<Content> toEntities(Step step, AIStepResponse stepResponse) {
        return null;
    };
}
