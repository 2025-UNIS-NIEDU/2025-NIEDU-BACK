package com.niedu.controller.attendance;
import com.niedu.global.response.ApiResponse;
import com.niedu.dto.attendance.AttendanceStreakRecord;
import com.niedu.service.user.AttendanceService;
import com.niedu.service.auth.AuthService;
import com.niedu.entity.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "출석", description = "출석 관련 API")
@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;
    private final AuthService authService;

    @Operation(
            summary = "출석 상황 조회. 홈 화면과 설정 화면에서 공용으로 사용.",
            description = "FUNCTION ID: HOM-HOME-01, SET-ALL-01"
    )
    @GetMapping("/streak")
    public ResponseEntity<ApiResponse<?>> getAttendanceStreak(
                                                               HttpServletRequest httpServletRequest
    ) {
        try {
            User user = authService.getUserFromRequest(httpServletRequest);
            Long userId = user.getId();

            int streak = attendanceService.calculateStreak(userId);
            AttendanceStreakRecord data = new AttendanceStreakRecord(streak);

            return ResponseEntity.ok(ApiResponse.success(data));

        } catch (Exception e) {
            return ResponseEntity
                    .internalServerError()
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "출석 정보 조회에 실패했습니다."));
        }
    }
}
