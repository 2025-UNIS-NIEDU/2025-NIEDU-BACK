package com.niedu.dto.course.user_answer;

import java.util.List;

public record SimpleAnswerListResponse(
        List<SimpleAnswerResponse> answers
) implements AnswerResponse{
}
