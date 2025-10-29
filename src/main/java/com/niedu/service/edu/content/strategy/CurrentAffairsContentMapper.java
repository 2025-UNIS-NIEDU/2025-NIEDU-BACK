package com.niedu.service.edu.content.strategy;

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
}
