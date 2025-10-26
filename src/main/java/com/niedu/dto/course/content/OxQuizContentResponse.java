package com.niedu.dto.course.content;

public record OxQuizContentResponse (
        Long contentId,
        String question,
        String correctAnswer,
        String answerExplanation
) {}