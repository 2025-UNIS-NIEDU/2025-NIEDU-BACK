package com.niedu.dto.course.ai;

import java.util.List;

public record AICourseResponse(
        Integer courseId,
        String topic,
        String subTopic,
        List<String> subTags,
        String courseName,
        String courseDescription,
        List<AISessionResponse> sessions
) {
}
