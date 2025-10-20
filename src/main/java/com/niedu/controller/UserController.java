package com.niedu.controller;

import com.niedu.dto.user.UserInfoResponse;
import com.niedu.entity.user.User;
import com.niedu.global.response.ApiResponse;
import com.niedu.service.AuthService;
import com.niedu.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final AuthService authService;
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<?>> getMyInfo(HttpServletRequest httpServletRequest) {
        User user = authService.getUserFromRequest(httpServletRequest);
        UserInfoResponse response = userService.getMyInfo(user);
        return (response != null)?
                ResponseEntity.ok(ApiResponse.success(response)):
                ResponseEntity.internalServerError().body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "내 정보 조회에 실패했습니다."));
    }
}
