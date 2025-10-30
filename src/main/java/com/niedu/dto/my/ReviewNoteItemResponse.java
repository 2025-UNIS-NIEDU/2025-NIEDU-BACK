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

        // --- 정렬을 위해 이 필드를 추가합니다 ---
        @Schema(description = "활동 생성 시간 (정렬용)")
        LocalDateTime createdAt
) {
        // 엔티티(예: SolvedProblem)로부터 DTO를 생성하는 정적 메서드 (예시)
    /*
    public static ReviewNoteItemResponse fromEntity(SolvedProblem problem) {

        String contentType = problem.getQuestion().getContentType().toString(); // "OX_QUIZ"
        Object contentDto = null;

        // --- content Type에 따라 실제 DTO를 생성 ---
        switch (contentType) {
            case "OX_QUIZ":
                // contentDto = OxQuizDto.fromEntity(problem.getQuestion()); // (예시)
                break;
            case "MULTIPLE_CHOICE":
                // contentDto = MultipleChoiceDto.fromEntity(problem.getQuestion()); // (예시)
                break;
            // ... (SHORT_ANSWER, SENTENCE_COMPLETION 등)
        }

        // (가정) 푼 시간
        LocalDateTime solvedAt = problem.getSolvedAt();

        return new ReviewNoteItemResponse(
            problem.getQuestion().getTopic().getName(),
            problem.getQuestion().getLevel().toString(), // "N", "I", "E"
            contentType,
            contentDto, // 실제 퀴즈 DTO
            solvedAt
        );
    }
    */
}