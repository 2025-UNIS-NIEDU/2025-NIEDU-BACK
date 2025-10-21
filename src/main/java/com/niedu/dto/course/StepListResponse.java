package com.niedu.dto.course;

import com.niedu.dto.course.content.ContentDto;
import com.niedu.dto.course.user_answer.UserAnswerDto;
import com.niedu.entity.course.StepType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class StepListResponse {
    private Integer stepId;
    private Integer stepOrder;
    private Boolean isCompleted;
    private StepType contentType;
    private ContentDto contentDto;
    private UserAnswerDto userAnswerDto;
}
