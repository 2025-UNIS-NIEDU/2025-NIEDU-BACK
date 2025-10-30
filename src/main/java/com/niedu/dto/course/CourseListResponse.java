package com.niedu.dto.course;

public record CourseListResponse (
    String title,
    String thumbnailUrl,
    String description,
    String topic,
    String subTopic
) {}
