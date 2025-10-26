package com.niedu.dto.course.content;

import java.util.ArrayList;
import java.util.List;

public record MultipleChoiceContentResponse (
        Long contentId,
        String question,
        List<MultipleChoiceOptionContent> options,
        String correctAnswer,
        String answerExplanation
) {}