package com.niedu.service.edu.content;

import com.niedu.dto.course.content.ContentResponse;
import com.niedu.entity.course.Step;
import com.niedu.service.edu.content.strategy.ContentMapperStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StepMapperService {

    private final List<ContentMapperStrategy> strategies;

    public ContentResponse toResponse(Step step) {
        // StepType에 따라 맞는 Mapper 선택
        return strategies.stream()
                .filter(strategy -> strategy.supports(step.getType()))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("No ContentMapper found for stepType: " + step.getType()))
                .toResponse(step);
    }
}
