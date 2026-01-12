package com.niedu.service.user;

import com.niedu.entity.user.AttendanceLog;
import com.niedu.entity.user.User;
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
        for (int i = logs.size() - 1; i > 0; i--) {
            LocalDate curr = logs.get(i).getAttendedDate();
            LocalDate prev = logs.get(i - 1).getAttendedDate();

            // 같은 날 출석 로그가 여러 개면 무시
            if (curr.equals(prev)) {
                continue;
            }
            // 하루 간격이면 streak++
            if (curr.equals(prev.plusDays(1))) {
                streak++;
            } else {
                break;
            }
        }

        return streak;
    }

    public void recordAttendance(User user, LocalDate attendedDate) {
        if (attendanceLogRepository.existsByUser_IdAndAttendedDate(user.getId(), attendedDate)) {
            return;
        }
        attendanceLogRepository.save(new AttendanceLog(user, attendedDate));
    }
}
