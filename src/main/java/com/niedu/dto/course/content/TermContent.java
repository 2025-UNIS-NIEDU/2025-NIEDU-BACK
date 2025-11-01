package com.niedu.dto.course.content;

import com.niedu.entity.content.Term;

public record TermContent(
        Long termId,
        String name,
        String definition,
        String exampleSentence,
        String additionalExplanation
) {
    public static TermContent fromEntity(Term term) {
        return new TermContent(
                term.getId(),
                term.getName(),
                term.getDefinition(),
                term.getExampleSentence(),
                term.getAdditionalExplanation()
        );
    }
}