package com.niedu.dto.course.is_correct;

public record IsCorrectResponse(
        Long contentId,
        Boolean isCorrect
) {
}
