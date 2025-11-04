package com.niedu.dto.course;

public record FeedbackAnswerRequest (
        Long contentId,
        String userAnswer
){}
