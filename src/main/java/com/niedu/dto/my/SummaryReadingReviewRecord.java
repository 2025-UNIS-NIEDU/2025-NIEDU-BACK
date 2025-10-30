package com.niedu.dto.my;

import com.niedu.dto.course.content.KeywordContent;
import com.niedu.entity.content.SummaryReading;
import com.niedu.entity.learning_record.user_answer.SummaryReadingAnswer;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "요약 읽기 - 복습 노트용 응답 DTO (Record)")
public record SummaryReadingReviewRecord(
        @Schema(description = "컨텐츠(문제) ID")
        Long contentId,

        @Schema(description = "원본 요약 지문")
        String summary,

        @Schema(description = "정답 키워드 목록")
        List<KeywordContent> correctKeywords,

        @Schema(description = "사용자가 선택한 키워드 목록")
        List<String> userKeywords
) {

    public static SummaryReadingReviewRecord fromEntities(
            SummaryReading content,
            SummaryReadingAnswer answer
    ) {
        return new SummaryReadingReviewRecord(
                content.getId(),
                content.getSummary(),
                content.getKeywords(),
                answer.getKeywords()
        );
    }
}