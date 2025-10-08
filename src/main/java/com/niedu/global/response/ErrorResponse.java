package com.niedu.global.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * ✅ 예외 응답 세부 구조
 * - GlobalExceptionHandler에서 사용
 */
@Getter
@Builder
public class ErrorResponse {
    private final boolean success = false;
    private final int status;
    private final String error;
    private final String message;
    private final String path;
    private final LocalDateTime timestamp = LocalDateTime.now();

    public static ErrorResponse of(int status, String error, String message, String path) {
        return ErrorResponse.builder()
                .status(status)
                .error(error)
                .message(message)
                .path(path)
                .build();
    }
}
