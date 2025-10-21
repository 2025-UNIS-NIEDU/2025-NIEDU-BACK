package com.niedu.dto.course;

public record FeedbackAnswerRequest (
        Integer questionIndex,
        String userAnswer
){}
