package com.niedu.dto.course.content;

import com.niedu.entity.content.ShortAnswerQuiz;

public record ShortAnswerContentResponse (
        Long contentId,
        String question,
        String correctAnswer,
        String answerExplanation
) implements ContentResponse {
    public static ShortAnswerContentResponse fromEntity(ShortAnswerQuiz entity) {
    return new ShortAnswerContentResponse(
            entity.getId(),
            entity.getQuestion(),
            entity.getCorrectAnswer(),
            entity.getAnswerExplanation()
    );
}}