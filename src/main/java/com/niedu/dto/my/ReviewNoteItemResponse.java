package com.niedu.dto.my;

import com.niedu.dto.course.content.ContentResponse;
import com.niedu.dto.course.user_answer.AnswerResponse;
import com.niedu.entity.course.Level; // --- 1. import Level Enum ---
import com.niedu.entity.course.StepType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "복습 노트 항목 응답 DTO")
public record ReviewNoteItemResponse(
        @Schema(description = "토픽명")
        String topic,

        @Schema(description = "난이도 (Enum)")
        Level level,

        @Schema(description = "컨텐츠 타입 (Enum)")
        StepType contentType,

        @Schema(description = "컨텐츠 상세 (질문 DTO)")
        ContentResponse content,

        @Schema(description = "사용자 답변 상세 (답변 DTO)")
        AnswerResponse answer,

        @Schema(description = "활동 생성 시간 (정렬용)")
        LocalDateTime createdAt
) {
}