package com.niedu.dto.course.user_answer;

import com.niedu.entity.learning_record.user_answer.SummaryReadingAnswer;

import java.util.List;

public record SummaryReadingAnswerResponse(
        Long contentId,
        List<String> keywords
) implements AnswerResponse {
    public static SummaryReadingAnswerResponse fromEntity(SummaryReadingAnswer entity) {
        return new SummaryReadingAnswerResponse(
                entity.getContent().getId(),
                entity.getKeywords()
        );
    }
}
