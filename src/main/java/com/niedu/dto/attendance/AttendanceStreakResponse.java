package com.niedu.dto.attendance;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceStreakResponse {
    private boolean success;
    private int status;
    private String message;
    private DataBody data;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DataBody {
        private Integer streak;
    }
}
