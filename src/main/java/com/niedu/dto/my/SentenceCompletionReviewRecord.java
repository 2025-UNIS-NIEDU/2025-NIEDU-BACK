package com.niedu.dto.my;

import com.niedu.entity.content.SentenceCompletionQuiz;
import com.niedu.entity.learning_record.user_answer.SentenceCompletionAnswer;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "문장 완성 퀴즈 - 복습 노트용 응답 DTO")
public record SentenceCompletionReviewRecord(
        @Schema(description = "컨텐츠(질문) ID")
        Long contentId,

        @Schema(description = "질문")
        String question,

        @Schema(description = "사용자 답변")
        String userAnswer,

        @Schema(description = "AI 채점 점수")
        Integer aiScore,

        @Schema(description = "AI 피드백")
        String aiFeedback
) {

    public static SentenceCompletionReviewRecord fromEntities(
            SentenceCompletionQuiz content,
            SentenceCompletionAnswer answer
    ) {
        return new SentenceCompletionReviewRecord(
                content.getId(),
                content.getQuestion(),
                answer.getUserAnswer(),
                answer.getAIScore(),
                answer.getAIFeedback()
        );
    }
}
