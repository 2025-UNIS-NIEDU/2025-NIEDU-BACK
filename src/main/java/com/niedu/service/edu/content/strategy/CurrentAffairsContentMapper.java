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
import java.util.Map;

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
                .map(raw -> (Map<String, Object>) raw)  // Map 형변환
                .map(map -> CurrentAffairs.builder()
                        .step(step)
                        .issue((String) map.get("issue"))
                        .cause((String) map.get("cause"))
                        .circumstance((String) map.get("circumstance"))
                        .result((String) map.get("result"))
                        .effect((String) map.get("effect"))
                        .build()
                )
                .map(content -> (Content) content)
                .toList();
    }
}
