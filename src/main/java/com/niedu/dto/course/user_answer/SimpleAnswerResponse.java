package com.niedu.dto.course.user_answer;

public record SimpleAnswerResponse(
        Long contentId,
        String value
) implements AnswerResponse { }
