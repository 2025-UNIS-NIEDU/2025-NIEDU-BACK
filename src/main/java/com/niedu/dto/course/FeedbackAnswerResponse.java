package com.niedu.dto.course;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class FeedbackAnswerResponse {
    private Integer AIScore;
    private String AIFeedback;
}
