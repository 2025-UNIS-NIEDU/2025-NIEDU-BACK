package com.niedu.service.edu.content.strategy;

import com.niedu.dto.course.content.ContentResponse;
import com.niedu.dto.course.content.SessionReflectionContentResponse;
import com.niedu.entity.content.Content;
import com.niedu.entity.content.SessionReflection;
import com.niedu.entity.course.Step;
import com.niedu.entity.course.StepType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SessionReflectionContentMapper implements ContentMapperStrategy {
    @Override
    public boolean supports(StepType type) {
        if (!type.equals(StepType.SESSION_REFLECTION)) return false;
        return true;
    }

    @Override
    public ContentResponse toResponse(Step step) {
        Content content = step.getContent();
        if (content instanceof SessionReflection sessionReflection) {
            return new SessionReflectionContentResponse(
                    sessionReflection.getQuestion()
            );
        }
        else {
            log.warn("이 Step은 SessionReflection 타입이 아닙니다: {}", content.getClass().getSimpleName());
            return null;
        }
    }
}
