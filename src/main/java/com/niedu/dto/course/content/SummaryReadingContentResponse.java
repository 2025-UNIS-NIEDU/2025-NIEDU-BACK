package com.niedu.dto.course.content;

import com.niedu.entity.content.SummaryReading;

import java.util.List;

public record SummaryReadingContentResponse (
        String summary,
        List<KeywordContent> keywords
) implements ContentResponse {
    public static SummaryReadingContentResponse fromEntity(SummaryReading entity) {
        return new SummaryReadingContentResponse(
                entity.getSummary(),
                entity.getKeywords()
        );
    }
}