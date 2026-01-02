package com.niedu.dto.course.ai;

public record AIFeedbackRequest(
        Long contentId,
        String referenceAnswer,
        String userAnswer
) {
}
