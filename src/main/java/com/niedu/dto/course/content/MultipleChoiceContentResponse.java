package com.niedu.dto.course.content;

import com.niedu.entity.content.MultipleChoiceQuiz;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;

public record MultipleChoiceContentResponse (
        Long contentId,
        String question,
        List<MultipleChoiceOptionContent> options,
        String correctAnswer,
        String answerExplanation
) {
    public static MultipleChoiceContentResponse fromEntity(MultipleChoiceQuiz entity) {
        List<MultipleChoiceOptionContent> optionsList = List.of(
                new MultipleChoiceOptionContent("A", entity.getOptionA()),
                new MultipleChoiceOptionContent("B", entity.getOptionB()),
                new MultipleChoiceOptionContent("C", entity.getOptionC()),
                new MultipleChoiceOptionContent("D", entity.getOptionD())
        );

        return new MultipleChoiceContentResponse(
                entity.getId(),
                entity.getQuestion(),
                optionsList,
                entity.getCorrectAnswer(),
                entity.getAnswerExplanation()
        );
    }
}