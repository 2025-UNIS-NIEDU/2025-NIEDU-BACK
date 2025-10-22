package com.niedu.service.edu.user_answer;

import com.niedu.dto.course.user_answer.AnswerResponse;
import com.niedu.entity.course.Step;
import com.niedu.service.edu.user_answer.strategy.UserAnswerMapperStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserAnswerMapperService {

    private final List<UserAnswerMapperStrategy> strategies;

    public AnswerResponse toResponse(Step step) {
        return strategies.stream()
                .filter(strategy -> strategy.supports(step.getType()))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("No UserAnswerMapper found for stepType: " + step.getType()))
                .toResponse(step);
    }
}
