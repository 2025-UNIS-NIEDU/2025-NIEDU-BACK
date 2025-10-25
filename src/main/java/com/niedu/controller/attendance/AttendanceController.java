package com.niedu.controller.attendance;

import com.niedu.global.response.ApiResponse;
import com.niedu.dto.attendance.AttendanceStreakRecord;
import com.niedu.service.user.AttendanceService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "출석", description = "출석 관련 API")
@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;


    @Operation(summary = "출석 상황 조회", description = "HOM-HOME-01/SET-ALL-01 명세서")
    @GetMapping("/streak")
    public ResponseEntity<ApiResponse<AttendanceStreakRecord>> getAttendanceStreak(
            @CookieValue(name = "accessToken") String accessToken
    ) {
        Long userId = 1L;

        int streak = attendanceService.calculateStreak(userId);

        AttendanceStreakRecord data = new AttendanceStreakRecord(streak);
        return ResponseEntity.ok(ApiResponse.success(data));
    }
}