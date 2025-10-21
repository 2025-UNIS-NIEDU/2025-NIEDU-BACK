package com.niedu.dto.course;

import lombok.*;

@Getter
@Setter
@Builder
public class CourseListResponse {
    private String title;
    private String thumbnailUrl;
    private String longDescription;
    private String topic;
}
