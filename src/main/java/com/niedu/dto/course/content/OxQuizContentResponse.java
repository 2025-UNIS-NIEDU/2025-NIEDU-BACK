package com.niedu.dto.course.content;

import com.niedu.entity.content.OxQuiz;
import io.swagger.v3.oas.annotations.media.Schema;

public record OxQuizContentResponse (
        Long contentId,
        String question,
        String correctAnswer,
        String answerExplanation
) {
    public static OxQuizContentResponse fromEntity(OxQuiz entity) {
        return new OxQuizContentResponse(
                entity.getId(),
                entity.getQuestion(),
                entity.getCorrectAnswer(),
                entity.getAnswerExplanation()
        );
    }
}