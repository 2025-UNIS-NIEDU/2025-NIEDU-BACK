package com.niedu.dto.course.content;

public record TermContent(
        Long termId,
        String name,
        String definition,
        String exampleSentence,
        String additionalExplanation
) {}