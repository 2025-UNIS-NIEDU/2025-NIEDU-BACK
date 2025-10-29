package com.niedu.service.edu.content.strategy;

import com.niedu.dto.course.content.ContentResponse;
import com.niedu.dto.course.content.SummaryReadingContentResponse;
import com.niedu.entity.content.Content;
import com.niedu.entity.content.SummaryReading;
import com.niedu.entity.course.Step;
import com.niedu.entity.course.StepType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class SummaryReadingContentMapper implements ContentMapperStrategy {
    @Override
    public boolean supports(StepType type) {
        if (!type.equals(StepType.SUMMARY_READING)) return false;
        return true;
    }

    @Override
    public ContentResponse toResponse(Step step, List<Content> contents) {
        if (contents == null || contents.isEmpty()) throw new RuntimeException("content 조회 실패");
        Content content = contents.get(0);

        if (content instanceof SummaryReading summaryReading)
            return new SummaryReadingContentResponse(
                    summaryReading.getSummary(),
                    summaryReading.getKeywords()
            );
        else {
            throw new RuntimeException("이 Step은 SummaryReading 타입이 아닙니다");
        }
    }
}
