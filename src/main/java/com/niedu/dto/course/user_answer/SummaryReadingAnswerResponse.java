package com.niedu.dto.course.user_answer;

import java.util.List;

public record SummaryReadingAnswerResponse(
    List<String> keywords
) implements AnswerResponse { }
