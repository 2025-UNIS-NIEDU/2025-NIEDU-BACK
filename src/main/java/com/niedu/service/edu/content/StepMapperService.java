package com.niedu.service.edu.content;

import com.niedu.dto.course.ai.AIStepResponse;
import com.niedu.dto.course.content.*;
import com.niedu.entity.content.Content;
import com.niedu.entity.course.Step;
import com.niedu.entity.course.StepType;
import com.niedu.service.edu.content.strategy.ContentMapperStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StepMapperService {

    private final List<ContentMapperStrategy> strategies;

    public ContentResponse toResponse(Step step, List<Content> contents) {
        if (contents == null || contents.isEmpty()) {
            return null;
        }

        StepType stepType = step.getType();

        ContentMapperStrategy strategy = strategies.stream()
                .filter(s -> s.supports(stepType))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("No ContentMapper found for stepType: " + stepType));
        return strategy.toResponse(step, contents);
    }

    public List<Content> toEntities(Step step, AIStepResponse stepResponse) {
        StepType stepType = step.getType();
        ContentMapperStrategy strategy = findStrategy(stepType);
        return strategy.toEntities(step, stepResponse);
    }

    private ContentMapperStrategy findStrategy(StepType stepType) {
        return strategies.stream()
                .filter(s -> s.supports(stepType))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("No ContentMapper found for stepType: " + stepType));
    }
}
