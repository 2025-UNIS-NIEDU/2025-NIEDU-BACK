package com.niedu.service.edu.content.strategy;

import com.niedu.dto.course.ai.AIStepResponse;
import com.niedu.dto.course.content.ContentResponse;
import com.niedu.dto.course.content.CurrentAffairsContentResponse;
import com.niedu.entity.content.Content;
import com.niedu.entity.content.CurrentAffairs;
import com.niedu.entity.course.Step;
import com.niedu.entity.course.StepType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class CurrentAffairsContentMapper implements ContentMapperStrategy{
    @Override
    public boolean supports(StepType type) {
        if (!type.equals(StepType.CURRENT_AFFAIRS)) return false;
        return true;
    }

    @Override
    public ContentResponse toResponse(Step step, List<Content> contents) {
        Content content = contents.get(0);
        if (content instanceof CurrentAffairs currentAffairs)
            return new CurrentAffairsContentResponse(
                    currentAffairs.getIssue(),
                    currentAffairs.getCause(),
                    currentAffairs.getCircumstance(),
                    currentAffairs.getResult(),
                    currentAffairs.getEffect()
            );
        else throw new RuntimeException("content 조회 실패");
    }

    @Override
    public List<Content> toEntities(Step step, AIStepResponse stepResponse) {
        return stepResponse.contents().stream()
                .filter(c -> c instanceof CurrentAffairsContentResponse)
                .map(c -> (CurrentAffairsContentResponse) c)
                .map(content -> CurrentAffairs.builder()
                        .step(step)
                        .issue(content.issue())
                        .cause(content.cause())
                        .circumstance(content.circumstance())
                        .result(content.result())
                        .effect(content.effect())
                        .build()
                )
                .map(currentAffairs -> (Content) currentAffairs)
                .toList();
    }
}
