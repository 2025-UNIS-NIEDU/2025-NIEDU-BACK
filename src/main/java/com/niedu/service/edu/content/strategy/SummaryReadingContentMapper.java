package com.niedu.service.edu.content.strategy;

import com.niedu.dto.course.ai.AIStepResponse;
import com.niedu.dto.course.content.ContentResponse;
import com.niedu.dto.course.content.KeywordContent;
import com.niedu.dto.course.content.SummaryReadingContentResponse;
import com.niedu.entity.content.Content;
import com.niedu.entity.content.SummaryReading;
import com.niedu.entity.course.Step;
import com.niedu.entity.course.StepType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

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

    @Override
    public List<Content> toEntities(Step step, AIStepResponse stepResponse) {
        return stepResponse.contents().stream()
                .map(raw -> (Map<String, Object>) raw)
                .map(map -> {
                    SummaryReading summary = SummaryReading.builder()
                            .step(step)
                            .summary((String) map.get("summary"))
                            .build();

                    // keywords: List<Map<String,Object>>
                    List<Map<String, Object>> keywords = (List<Map<String, Object>>) map.get("keywords");
                    summary.setKeywords(
                            keywords.stream()
                                    .map(k -> new KeywordContent(
                                            (String) k.get("word"),
                                            (Boolean) k.get("isTopicWord")
                                    ))
                                    .toList()
                    );

                    return summary;
                })
                .map(c -> (Content) c)
                .toList();
    }
}
