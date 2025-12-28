package com.niedu.global.exception;

import com.niedu.global.response.ApiResponse;
import com.niedu.global.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** 🔹 유효성 검증 실패 (DTO @Valid) */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<?> handleValidationException(MethodArgumentNotValidException e, HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            sb.append(String.format("[%s] %s ", fieldError.getField(), fieldError.getDefaultMessage()));
        }
        log.warn("❗ Validation Error: {}", sb);
        return ApiResponse.error(HttpStatus.BAD_REQUEST, sb.toString());
    }

    /** 🔹 요청 파라미터 누락 */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ApiResponse<?> handleMissingParameter(MissingServletRequestParameterException e) {
        log.warn("❗ Missing Request Parameter: {}", e.getParameterName());
        return ApiResponse.error(HttpStatus.BAD_REQUEST, "필수 요청 파라미터가 누락되었습니다: " + e.getParameterName());
    }

    /** 🔹 인증 실패 */
    @ExceptionHandler(BadCredentialsException.class)
    public ApiResponse<?> handleBadCredentials(BadCredentialsException e) {
        log.warn("❗ 인증 실패: {}", e.getMessage());
        return ApiResponse.error(HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 올바르지 않습니다.");
    }

    /** 🔹 접근 권한 없음 (Spring Security) */
    @ExceptionHandler(AccessDeniedException.class)
    public ApiResponse<?> handleAccessDenied(AccessDeniedException e) {
        log.warn("❗ 접근 거부: {}", e.getMessage());
        return ApiResponse.error(HttpStatus.FORBIDDEN, "접근 권한이 없습니다.");
    }

    /** 🔹 접근 권한 없음 (Java NIO - 파일 접근 등) */
    @ExceptionHandler(java.nio.file.AccessDeniedException.class)
    public ApiResponse<?> handleFileAccessDenied(java.nio.file.AccessDeniedException e) {
        log.warn("❗ 파일 접근 거부: {}", e.getMessage());
        return ApiResponse.error(HttpStatus.FORBIDDEN, "파일 접근이 거부되었습니다.");
    }

    /** 🔹 데이터 무결성 제약 위반 */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ApiResponse<?> handleDataIntegrityViolation(DataIntegrityViolationException e) {
        log.error("❗ DB 제약조건 위반: {}", e.getMessage());
        return ApiResponse.error(HttpStatus.CONFLICT, "데이터 무결성 제약조건에 위배되었습니다.");
    }

    /** 🔹 일반 런타임 에러 */
    @ExceptionHandler(RuntimeException.class)
    public ApiResponse<?> handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        log.error("🔥 RuntimeException 발생: {}", e.getMessage(), e);
        return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "서버에서 예기치 못한 오류가 발생했습니다.");
    }

    /** 🔹 잘못된 요청 */
    @ExceptionHandler(IllegalArgumentException.class)
    public ApiResponse<?> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("❗ Invalid argument: {}", e.getMessage());
        return ApiResponse.error(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    /** 🔹 그 외 모든 예외 */
    @ExceptionHandler(Exception.class)
    public ApiResponse<?> handleException(Exception e, HttpServletRequest request) {
        log.error("🔥 Unhandled Exception: {}", e.getMessage(), e);
        ErrorResponse errorResponse = ErrorResponse.of(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                e.getClass().getSimpleName(),
                e.getMessage(),
                request.getRequestURI()
        );
        return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, errorResponse.getMessage());
    }
}
