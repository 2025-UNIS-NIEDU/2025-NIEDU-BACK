package com.niedu.dto.my;

import com.niedu.entity.content.ShortAnswerQuiz;
import com.niedu.entity.learning_record.user_answer.SimpleAnswer;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "단답형 퀴즈 - 복습 노트용 응답 DTO (Record)")
public record ShortAnswerReviewRecord(
        @Schema(description = "컨텐츠(질문) ID")
        Long contentId,

        @Schema(description = "질문")
        String question,

        @Schema(description = "정답")
        String correctAnswer,

        @Schema(description = "해설")
        String answerExplanation,

        @Schema(description = "사용자 답변")
        String userAnswer
) {

    public static ShortAnswerReviewRecord fromEntities(
            ShortAnswerQuiz content,
            SimpleAnswer answer
    ) {
        return new ShortAnswerReviewRecord(
                content.getId(),
                content.getQuestion(),
                content.getCorrectAnswer(),
                content.getAnswerExplanation(),
                answer.getValue()
        );
    }
}