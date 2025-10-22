package com.niedu.dto.course;

import java.time.Duration;
import java.time.LocalDateTime;

public record SessionSummaryResponse (
        Integer streak,
        Duration learningTime
) {}