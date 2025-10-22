package com.niedu.service.edu.content.strategy;

import com.niedu.dto.course.content.ContentResponse;
import com.niedu.entity.course.Step;
import com.niedu.entity.course.StepType;

// Step 엔티티 → ContentResponse DTO 변환을 위한 전략 인터페이스
public interface ContentMapperStrategy {
    boolean supports(StepType type);
    ContentResponse toResponse(Step step);
}
