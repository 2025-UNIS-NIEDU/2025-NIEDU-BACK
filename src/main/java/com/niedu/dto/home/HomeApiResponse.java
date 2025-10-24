package com.niedu.dto.home;

import lombok.Getter;

@Getter
public class HomeApiResponse<T> {

    private final boolean success;
    private final int status;
    private final String message;
    private final T data;

    private HomeApiResponse(T data) {
        this.success = true;
        this.status = 200;
        this.message = "요청이 성공적으로 처리되었습니다.";
        this.data = data;
    }

    public static <T> HomeApiResponse<T> success(T data) {
        return new HomeApiResponse<>(data);
    }
}