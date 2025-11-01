package com.niedu.dto.course.content;

import com.niedu.entity.content.OxQuiz;

public record OxQuizContentResponse (
        Long contentId,
        String question,
        String correctAnswer,
        String answerExplanation
) implements ContentResponse {

    public static OxQuizContentResponse fromEntity(OxQuiz entity) {
        return new OxQuizContentResponse(
                entity.getId(),
                entity.getQuestion(),
                entity.getCorrectAnswer(),
                entity.getAnswerExplanation()
        );
    }
}