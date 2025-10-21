package com.niedu.dto.course;


public record CourseResponse (
        String thumbnailUrl,
        String title,
        String topic,
        Float progress,
        String longDescription
){}