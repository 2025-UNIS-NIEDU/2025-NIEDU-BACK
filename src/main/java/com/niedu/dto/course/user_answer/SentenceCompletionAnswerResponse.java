package com.niedu.dto.course.user_answer;

public record SentenceCompletionAnswerResponse(
        Long contentId,
        String userAnswer,
        Integer AIScore,
        String AIFeedback
) {}
