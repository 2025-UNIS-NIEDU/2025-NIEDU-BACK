package com.niedu.service.edu.user_answer.strategy;

import com.niedu.dto.course.user_answer.AnswerResponse;
import com.niedu.entity.course.Step;
import com.niedu.entity.course.StepType;
import com.niedu.entity.learning_record.StudiedStep;
import com.niedu.entity.learning_record.user_answer.UserAnswer;
import com.niedu.entity.user.User;
import org.springframework.stereotype.Component;

@Component
public class SentenceCompletionAnswerMapper implements UserAnswerMapperStrategy{
    @Override
    public boolean supports(StepType type) {
        return false;
    }

    public AnswerResponse toResponse(StudiedStep studiedStep) {
        return null;
    }

    @Override
    public UserAnswer toEntity(User user, Step step, AnswerResponse request) {
        return null;
    }
}
