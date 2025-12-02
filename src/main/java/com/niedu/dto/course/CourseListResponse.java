package com.niedu.dto.course;

public record CourseListResponse (
    Long id,
    String title,
    String thumbnailUrl,
    String description,
    String topic,
    String subTopic
) {}
