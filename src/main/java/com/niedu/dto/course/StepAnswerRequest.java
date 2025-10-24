package com.niedu.dto.course;

import com.niedu.dto.course.user_answer.AnswerResponse;
import com.niedu.entity.course.StepType;

public record StepAnswerRequest (
        StepType contentType,
        AnswerResponse userAnswer
) {}