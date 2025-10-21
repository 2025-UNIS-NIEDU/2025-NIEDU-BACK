package com.niedu.dto.course;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class SessionSummaryResponse {
    private Integer streak;
    private LocalDateTime learningTime;
}