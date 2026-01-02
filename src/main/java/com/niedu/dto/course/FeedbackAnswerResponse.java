package com.niedu.dto.course;

public record FeedbackAnswerResponse (
        Long contentId,
        Integer AIScore,
        String AIFeedback
) {}