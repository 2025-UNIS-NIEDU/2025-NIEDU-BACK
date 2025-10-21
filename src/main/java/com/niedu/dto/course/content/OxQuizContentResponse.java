package com.niedu.dto.course.content;

public record OxQuizContentResponse (
        String question,
        String correctAnswer,
        String answerExplanation,
        String sourceUrl
) implements ContentResponse {}