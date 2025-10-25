package com.niedu.dto.course.content;

public record SentenceCompletionContentResponse (
        Long contentId,
        String question
) implements ContentResponse {}