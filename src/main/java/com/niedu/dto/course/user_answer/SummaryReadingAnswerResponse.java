package com.niedu.dto.course.user_answer;

import java.util.List;

public record SummaryReadingAnswerResponse(
        Long contentId,
        List<String> keywords
) implements AnswerResponse { }
