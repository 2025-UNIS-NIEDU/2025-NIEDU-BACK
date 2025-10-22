package com.niedu.repository.user;

import com.niedu.entity.user.AttendanceLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AttendanceLogRepository extends JpaRepository<AttendanceLog, Long> {
    List<AttendanceLog> findAllByUser_IdOrderByAttendedDateAsc(Long userId);
    boolean existsByUser_IdAndAttendedDate(Long userId, LocalDate date);
}
