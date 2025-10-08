package com.niedu.global.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private int status;
    private String message;
    private T data;

    /** ✅ 성공 응답 (데이터 포함) */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, HttpStatus.OK.value(), "요청이 성공적으로 처리되었습니다.", data);
    }

    /** ✅ 성공 응답 (메시지만) */
    public static ApiResponse<?> successMessage(String message) {
        return new ApiResponse<>(true, HttpStatus.OK.value(), message, null);
    }

    /** ✅ 커스텀 성공 응답 */
    public static <T> ApiResponse<T> success(HttpStatus status, String message, T data) {
        return new ApiResponse<>(true, status.value(), message, data);
    }

    /** ❌ 실패 응답 */
    public static ApiResponse<?> error(HttpStatus status, String message) {
        return new ApiResponse<>(false, status.value(), message, null);
    }

    /** ❌ 예외 기반 실패 응답 */
    public static ApiResponse<?> error(Exception e) {
        return new ApiResponse<>(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), null);
    }
}
