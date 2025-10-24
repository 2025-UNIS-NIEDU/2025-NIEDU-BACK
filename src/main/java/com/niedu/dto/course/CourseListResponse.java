package com.niedu.dto.course;

public record CourseListResponse (
    String title,
    String thumbnailUrl,
    String longDescription,
    String topic,
    String subTopic
) {}
