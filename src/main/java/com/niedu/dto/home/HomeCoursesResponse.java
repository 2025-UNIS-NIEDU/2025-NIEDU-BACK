package com.niedu.dto.home;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HomeCoursesResponse {

    private boolean success;
    private int status;
    private String message;
    private List<DataBody> data;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DataBody {
        private String thumbnailUrl;
        private String title;
        private String longDescription;
        private String topic;
    }
}
