package com.niedu.controller.attendance;

import com.niedu.dto.attendance.AttendanceStreakRequest;
import com.niedu.dto.attendance.AttendanceStreakResponse;
import com.niedu.service.attendance.AttendanceResponseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceResponseService attendanceService;

    @GetMapping("/streak")
    public ResponseEntity<AttendanceStreakResponse> getStreak(
            @CookieValue(name = "accessToken", required = false) String accessToken,
            @RequestBody(required = false) AttendanceStreakRequest request
    ) {
        if (accessToken == null) {
            return ResponseEntity.status(401).body(
                    AttendanceStreakResponse.builder()
                            .success(false)
                            .status(401)
                            .message("유효하지 않은 또는 만료된 토큰입니다.")
                            .data(null)
                            .build()
            );
        }

        // 토큰 검증 후 userId 추출 (예시)
        Long userId = extractUserIdFromToken(accessToken);

        AttendanceStreakResponse response = attendanceService.getAttendanceStreak(userId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    private Long extractUserIdFromToken(String token) {
        // JWT 파싱 또는 사용자 정보 추출 로직 (예시)
        return 1L;
    }
}
