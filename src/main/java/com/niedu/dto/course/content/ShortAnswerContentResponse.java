package com.niedu.dto.course.content;

public record ShortAnswerContentResponse (
        Long contentId,
        String question,
        String correctAnswer,
        String answerExplanation
) {}