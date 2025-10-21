package com.niedu.dto.course;

import java.time.LocalDateTime;

public record SessionSummaryResponse (
        Integer streak,
        LocalDateTime learningTime
) {}