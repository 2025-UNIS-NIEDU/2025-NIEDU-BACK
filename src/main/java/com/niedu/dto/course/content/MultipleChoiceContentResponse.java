package com.niedu.dto.course.content;

import java.util.ArrayList;

public record MultipleChoiceContentResponse (
        String question,
        ArrayList<MultipleChoiceOptionContent> options,
        String correctAnswer,
        String answerExplanation,
        String sourceUrl
) implements ContentResponse {}