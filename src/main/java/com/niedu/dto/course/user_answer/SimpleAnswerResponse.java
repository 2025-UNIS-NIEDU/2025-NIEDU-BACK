package com.niedu.dto.course.user_answer;

import com.niedu.entity.learning_record.user_answer.SimpleAnswer;

public record SimpleAnswerResponse(
        Long contentId,
        String value
) implements AnswerResponse {
    public static SimpleAnswerResponse fromEntity(SimpleAnswer entity) {
        return new SimpleAnswerResponse(
                entity.getContent().getId(),
                entity.getValue()
        );
    }
}
