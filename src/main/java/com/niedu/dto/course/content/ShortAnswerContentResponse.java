package com.niedu.dto.course.content;

public record ShortAnswerContentResponse (
        String question,
        String correctAnswer,
        String answerExplanation,
        String sourceUrl
) implements ContentResponse {}