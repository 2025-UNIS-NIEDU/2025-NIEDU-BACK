package com.niedu.dto.course;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CourseResponse {
    private String thumbnailUrl;
    private String title;
    private String topic;
    private Float progress;
    private String longDescription;
}
