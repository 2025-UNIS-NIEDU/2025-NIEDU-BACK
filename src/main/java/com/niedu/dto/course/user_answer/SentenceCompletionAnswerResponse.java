package com.niedu.dto.course.user_answer;

import com.niedu.entity.learning_record.user_answer.SentenceCompletionAnswer;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "문장 완성 답변 응답 DTO")
public record SentenceCompletionAnswerResponse(
        Long contentId,
        String userAnswer,
        Integer AIScore,
        String AIFeedback
) implements AnswerResponse {
    public static SentenceCompletionAnswerResponse fromEntity(SentenceCompletionAnswer entity) {
        return new SentenceCompletionAnswerResponse(
                entity.getContent().getId(),
                entity.getUserAnswer(),
                entity.getAIScore(),
                entity.getAIFeedback()
        );
    }
}