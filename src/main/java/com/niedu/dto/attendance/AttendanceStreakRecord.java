package com.niedu.dto.attendance;


import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "출석 현황 데이터 Record")
public record AttendanceStreakRecord(
        @Schema(description = "연속 출석일", example = "5")
        int streak
) {}