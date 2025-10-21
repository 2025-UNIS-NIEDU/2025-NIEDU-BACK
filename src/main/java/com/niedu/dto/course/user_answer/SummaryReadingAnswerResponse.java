package com.niedu.dto.course.user_answer;

import java.util.ArrayList;

public record SummaryReadingAnswerResponse(
    ArrayList<String> keywords
) implements AnswerResponse { }
