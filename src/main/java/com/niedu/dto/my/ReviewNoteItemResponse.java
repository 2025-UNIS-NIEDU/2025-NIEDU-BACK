package com.niedu.dto.my;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime; // --- LocalDateTime import 추가 ---

@Schema(description = "복습 노트 항목 응답 DTO")
public record ReviewNoteItemResponse(
        @Schema(description = "토픽명")
        String topic,

        @Schema(description = "난이도 (N, I, E)")
        String level,

        @Schema(description = "컨텐츠 타입 (예: OX_QUIZ, MULTIPLE_CHOICE)")
        String contentType,

        @Schema(description = "컨텐츠 상세 (타입에 맞는 실제 퀴즈 DTO가 담김)")
        Object content,

        @Schema(description = "활동 생성 시간 (정렬용)")
        LocalDateTime createdAt
) {
}