package com.niedu.dto.home;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HomeNewsResponse {

        private boolean success;
        private int status;
        private String message;
        private DataBody data;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class DataBody {
                private String thumbnailUrl;
                private String title;
                private String publisher;
                private String topic;
        }
}

