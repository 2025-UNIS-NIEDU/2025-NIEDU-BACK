package com.niedu.dto.course;

import com.fasterxml.jackson.databind.JsonNode;
import com.niedu.entity.course.StepType;

public record StepAnswerRequest (
        StepType contentType,
        JsonNode userAnswer
) {}
