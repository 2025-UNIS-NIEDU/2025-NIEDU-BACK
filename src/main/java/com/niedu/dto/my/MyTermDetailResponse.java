package com.niedu.dto.my;

import com.niedu.entity.content.Term;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "용어 사전 상세 조회 응답 DTO")
public record MyTermDetailResponse(
        @Schema(description = "용어 ID")
        Long termId,
        @Schema(description = "용어명")
        String name,
        @Schema(description = "용어 정의")
        String definition,
        @Schema(description = "용례 (예시 문장)")
        String exampleSentence,
        @Schema(description = "추가 설명")
        String additionalExplanation
) {
    public static MyTermDetailResponse fromEntity(Term term) {
        return new MyTermDetailResponse(
                term.getId(),
                term.getName(),
                term.getDefinition(),
                term.getExampleSentence(),
                term.getAdditionalExplanation()
        );
    }
}