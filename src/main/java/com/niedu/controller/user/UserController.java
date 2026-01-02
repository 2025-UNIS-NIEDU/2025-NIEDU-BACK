package com.niedu.controller.user;

import com.niedu.dto.user.UserInfoResponse;
import com.niedu.entity.user.User;
import com.niedu.global.response.ApiResponse;
import com.niedu.service.auth.AuthService;
import com.niedu.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "회원관리", description = "회원/인증 관련 API")
public class UserController {
    private final AuthService authService;
    private final UserService userService;

    @Operation(
            summary = "회원 정보 조회. 홈 화면과 설정 화면에서 공용으로 사용.",
            description = "FUNCTION ID: ONB-TOPIC-01, SET-ALL-01"
    )
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<?>> getMyInfo(HttpServletRequest httpServletRequest) {
        User user = authService.getUserFromRequest(httpServletRequest);
        UserInfoResponse response = userService.getMyInfo(user);
        return (response != null)?
                ResponseEntity.ok(ApiResponse.success(response)):
                ResponseEntity.internalServerError().body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "내 정보 조회에 실패했습니다."));
    }
}
