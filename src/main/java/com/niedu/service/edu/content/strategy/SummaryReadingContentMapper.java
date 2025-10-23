package com.niedu.service.edu.content.strategy;

import com.niedu.dto.course.content.ContentResponse;
import com.niedu.dto.course.content.SummaryReadingContentResponse;
import com.niedu.entity.content.Content;
import com.niedu.entity.content.SummaryReading;
import com.niedu.entity.course.Step;
import com.niedu.entity.course.StepType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SummaryReadingContentMapper implements ContentMapperStrategy {
    @Override
    public boolean supports(StepType type) {
        if (!type.equals(StepType.SUMMARY_READING)) return false;
        return true;
    }

    @Override
    public ContentResponse toResponse(Step step) {
        Content content = step.getContent();
        if (content instanceof SummaryReading summaryReading) {
            return new SummaryReadingContentResponse(
                    summaryReading.getSummary(),
                    summaryReading.getKeywords()
            );
        }
        else {
            log.warn("이 Step은 SummaryReading 타입이 아닙니다: {}", content.getClass().getSimpleName());
            return null;
        }
    }
}
