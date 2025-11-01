package com.niedu.dto.my;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "캘린더 날짜 내비게이터 응답 DTO")
public record DateNavigatorResponse(
        @Schema(description = "현재 연도")
        Integer currentYear,
        @Schema(description = "현재 월")
        Integer currentMonth,
        @Schema(description = "날짜 범위")
        Range range,
        @Schema(description = "가장 이른 학습 활동 시간")
        LocalDateTime earliestLearning,
        @Schema(description = "가장 늦은 학습 활동 시간")
        LocalDateTime latestLearning
) {
    @Schema(description = "날짜 범위 상세")
    public record Range(
            @Schema(description = "범위 시작 시간")
            LocalDateTime start,
            @Schema(description = "범위 종료 시간")
            LocalDateTime end
    ) {}
}