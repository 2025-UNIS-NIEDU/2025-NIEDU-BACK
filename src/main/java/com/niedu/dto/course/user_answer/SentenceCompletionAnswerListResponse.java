package com.niedu.dto.course.user_answer;

import java.util.List;

public record SentenceCompletionAnswerListResponse(
        List<SentenceCompletionAnswerResponse> answers
) implements AnswerResponse{
}
