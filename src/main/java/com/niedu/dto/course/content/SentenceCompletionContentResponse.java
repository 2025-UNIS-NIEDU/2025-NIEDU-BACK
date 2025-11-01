package com.niedu.dto.course.content;

import com.niedu.entity.content.SentenceCompletionQuiz;

public record SentenceCompletionContentResponse (
        Long contentId,
        String question
) implements ContentResponse {
    public static SentenceCompletionContentResponse fromEntity(SentenceCompletionQuiz entity) {
    return new SentenceCompletionContentResponse(
            entity.getId(),
            entity.getQuestion()
    );
}}