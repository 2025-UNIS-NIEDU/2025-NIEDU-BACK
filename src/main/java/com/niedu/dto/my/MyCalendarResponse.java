package com.niedu.dto.my;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "캘린더 응답 DTO")
public record MyCalendarResponse(
        @Schema(description = "조회 연도")
        Integer year,
        @Schema(description = "조회 월")
        Integer month,
        @Schema(description = "날짜별 상세 정보 목록")
        List<DayDetail> days
) {
    @Schema(description = "날짜별 상세 정보")
    public record DayDetail(
            @Schema(description = "해당 날짜")
            LocalDateTime date,
            @Schema(description = "코스/활동 정보 목록")
            List<MyCalendarCourseInfo> courses
    ) {}
}