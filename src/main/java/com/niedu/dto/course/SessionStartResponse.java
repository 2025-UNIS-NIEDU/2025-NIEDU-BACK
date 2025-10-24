package com.niedu.dto.course;

import java.util.ArrayList;

public record SessionStartResponse (
        Long entryStepId,
        ArrayList<StepListResponse> steps,
        Float progress
) {}