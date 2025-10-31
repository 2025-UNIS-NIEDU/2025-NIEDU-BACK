package com.niedu.dto.course.user_answer;

import com.niedu.entity.learning_record.SharedResponse;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "세션 돌아보기 답변 응답 DTO")
public record SharedReflectionResponse(
        @Schema(description = "스텝 ID")
        Long stepId,

        @Schema(description = "사용자 답변 (세션 돌아보기 내용)")
        String userResponse
) implements AnswerResponse {


    public static SharedReflectionResponse fromEntity(SharedResponse entity) {
        return new SharedReflectionResponse(
                entity.getStep().getId(),
                entity.getUserResponse()
        );
    }
}