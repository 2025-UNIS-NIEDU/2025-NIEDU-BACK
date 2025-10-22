package com.niedu.service.edu.content.strategy;

import com.niedu.dto.course.content.ContentResponse;
import com.niedu.entity.course.Step;
import com.niedu.entity.course.StepType;
import org.springframework.stereotype.Component;

@Component
public class ShortAnswerContentMapper implements ContentMapperStrategy{
    @Override
    public boolean supports(StepType type) {
        return false;
    }

    @Override
    public ContentResponse toResponse(Step step) {
        return null;
    }
}
