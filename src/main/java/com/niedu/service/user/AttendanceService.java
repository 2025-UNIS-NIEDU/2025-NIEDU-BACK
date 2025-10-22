package com.niedu.service.user;

import com.niedu.entity.user.AttendanceLog;
import com.niedu.repository.user.AttendanceLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceService {
    private final AttendanceLogRepository attendanceLogRepository;

    public int calculateStreak(Long userId) {

        List<AttendanceLog> logs = attendanceLogRepository.findAllByUser_IdOrderByAttendedDateAsc(userId);
        if (logs.isEmpty()) return 0;

        int streak = 1;
        int maxStreak = 1;

        for (int i = 1; i < logs.size(); i++) {
            LocalDate prev = logs.get(i - 1).getAttendedDate();
            LocalDate curr = logs.get(i).getAttendedDate();

            // 하루 간격이면 streak++
            if (curr.equals(prev.plusDays(1))) {
                streak++;
            }
            // 같은 날 출석 로그가 여러 개면 무시
            else if (!curr.equals(prev)) {
                streak = 1;
            }

            maxStreak = Math.max(maxStreak, streak);
        }

        return maxStreak;
    }

}
