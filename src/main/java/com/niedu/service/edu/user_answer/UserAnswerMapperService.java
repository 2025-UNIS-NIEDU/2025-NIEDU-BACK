package com.niedu.service.edu.user_answer;

import com.niedu.dto.course.StepAnswerRequest;
import com.niedu.dto.course.user_answer.AnswerResponse;
import com.niedu.entity.course.Step;
import com.niedu.entity.learning_record.StudiedStep;
import com.niedu.entity.learning_record.user_answer.UserAnswer;
import com.niedu.entity.user.User;
import com.niedu.service.edu.user_answer.strategy.UserAnswerMapperStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserAnswerMapperService {

    private final List<UserAnswerMapperStrategy> strategies;

    public AnswerResponse toResponse(StudiedStep studiedStep) {
        return strategies.stream()
                .filter(strategy -> strategy.supports(studiedStep.getStep().getType()))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("No UserAnswerMapper found for stepType: " + studiedStep.getStep().getType()))
                .toResponse(studiedStep);
    }

    public UserAnswer toEntity(User user, Step step, AnswerResponse request) {
        return strategies.stream()
                .filter(strategy -> strategy.supports(step.getType()))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("No UserAnswerMapper found for stepType: " + step.getType()))
                .toEntity(user, step, request);
    }
}
