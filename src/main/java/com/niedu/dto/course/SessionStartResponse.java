package com.niedu.dto.course;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
@Builder
public class SessionStartResponse {
    private Long entryStepId;
    private ArrayList<StepListResponse> steps;
    private Float progress;
}
