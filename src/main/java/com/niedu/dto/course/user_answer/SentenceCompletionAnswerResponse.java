package com.niedu.dto.course.user_answer;

public record SentenceCompletionAnswerResponse(
        Integer questionIndex,
        String userAnswer,
        Integer AIScore,
        String AIFeedback
) implements AnswerResponse {}
