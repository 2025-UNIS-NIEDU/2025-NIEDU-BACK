package com.niedu.dto.course.ai;

import java.util.List;

public record AICourseListResponse(
        List<AICourseResponse> courses
) {
}
