package com.niedu.service.edu.content.strategy;

import com.niedu.dto.course.ai.AIStepResponse;
import com.niedu.dto.course.content.ContentResponse;
import com.niedu.dto.course.content.SessionReflectionContentResponse;
import com.niedu.entity.content.Content;
import com.niedu.entity.content.SessionReflection;
import com.niedu.entity.course.Step;
import com.niedu.entity.course.StepType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class SessionReflectionContentMapper implements ContentMapperStrategy {
    @Override
    public boolean supports(StepType type) {
        if (!type.equals(StepType.SESSION_REFLECTION)) return false;
        return true;
    }

    @Override
    public ContentResponse toResponse(Step step, List<Content> contents) {
        Content content = contents.get(0);
        if (content instanceof SessionReflection sessionReflection)
            return new SessionReflectionContentResponse(
                    sessionReflection.getQuestion()
            );
        else throw new RuntimeException("content 조회 실패");
    }

    @Override
    public List<Content> toEntities(Step step, AIStepResponse stepResponse) {
        return stepResponse.contents().stream()
                .filter(c -> c instanceof SessionReflectionContentResponse)
                .map(c -> (SessionReflectionContentResponse) c)
                .map(content -> SessionReflection.builder()
                        .step(step)
                        .question(content.question())
                        .build())
                .map(sessionReflection -> (Content) sessionReflection)
                .toList();
    }
}
