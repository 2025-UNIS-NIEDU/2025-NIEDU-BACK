package com.niedu.dto.course.ai;

import java.time.LocalDateTime;
import java.util.List;

public record AISessionResponse(
        Integer sessionId,
        String headline,
        LocalDateTime publishedAt,
        String publisher,
        String sourceUrl,
        String summary,
        String thumbnailUrl,
        List<AIQuizResponse> quizzes
) {
}
