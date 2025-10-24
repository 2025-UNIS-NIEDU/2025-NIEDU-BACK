package com.niedu.service.attendance;

import com.niedu.dto.attendance.AttendanceStreakResponse;
import org.springframework.stereotype.Service;

@Service
public class AttendanceResponseService {

    public AttendanceStreakResponse getAttendanceStreak(Long userId) {
        try {
            int streak = getMockStreak(userId);

            return AttendanceStreakResponse.builder()
                    .success(true)
                    .status(200)
                    .message("요청이 성공적으로 처리되었습니다.")
                    .data(AttendanceStreakResponse.DataBody.builder()
                            .streak(streak)
                            .build())
                    .build();

        } catch (Exception e) {
            return AttendanceStreakResponse.builder()
                    .success(false)
                    .status(500)
                    .message(e.getMessage())
                    .data(null)
                    .build();
        }
    }

    private int getMockStreak(Long userId) {
        return 5;
    }
}
