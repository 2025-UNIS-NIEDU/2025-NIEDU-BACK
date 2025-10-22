package com.niedu.service.edu.user_answer.strategy;

import com.niedu.dto.course.user_answer.AnswerResponse;
import com.niedu.entity.course.Step;
import com.niedu.entity.course.StepType;
import com.niedu.entity.learning_record.StudiedStep;
import com.niedu.entity.learning_record.user_answer.UserAnswer;
import com.niedu.entity.user.User;

public interface UserAnswerMapperStrategy {
    boolean supports(StepType type);
    AnswerResponse toResponse(StudiedStep studiedStep);

    UserAnswer toEntity(User user, Step step, AnswerResponse request);
}
