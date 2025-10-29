package com.niedu.dto.course;

import com.niedu.dto.course.content.ContentResponse;
import com.niedu.dto.course.is_correct.IsCorrectResponse;
import com.niedu.dto.course.user_answer.AnswerResponse;
import com.niedu.entity.course.StepType;

import java.util.List;

public record StepListResponse (
        Long stepId,
        Integer stepOrder,
        Boolean isCompleted,
        StepType contentType,
        ContentResponse content,
        AnswerResponse userAnswer,
        List<IsCorrectResponse> isCorrect
) {}