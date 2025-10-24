package com.niedu.service.edu.content.strategy;

import com.niedu.dto.course.content.ContentResponse;
import com.niedu.dto.course.content.CurrentAffairsContentResponse;
import com.niedu.entity.content.Content;
import com.niedu.entity.content.CurrentAffairs;
import com.niedu.entity.course.Step;
import com.niedu.entity.course.StepType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CurrentAffairsContentMapper implements ContentMapperStrategy{
    @Override
    public boolean supports(StepType type) {
        if (!type.equals(StepType.CURRENT_AFFAIRS)) return false;
        return true;
    }

    @Override
    public ContentResponse toResponse(Step step) {
        Content content = step.getContent();
        if (content instanceof CurrentAffairs currentAffairs) {
            return new CurrentAffairsContentResponse(
                    currentAffairs.getIssue(),
                    currentAffairs.getCause(),
                    currentAffairs.getCircumstance(),
                    currentAffairs.getCircumstance(),
                    currentAffairs.getEffect()
            );
        } else {
            log.warn("이 Step은 CurrentAffairs 타입이 아닙니다: {}", content.getClass().getSimpleName());
            return null;
        }
    }
}
