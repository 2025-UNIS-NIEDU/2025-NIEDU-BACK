package com.niedu.service.edu.user_answer;

import com.niedu.dto.course.StepAnswerRequest;
import com.niedu.dto.course.user_answer.AnswerResponse;
import com.niedu.entity.course.Step;
import com.niedu.entity.course.StepType;
import com.niedu.entity.learning_record.StudiedStep;
import com.niedu.entity.learning_record.user_answer.UserAnswer;
import com.niedu.entity.user.User;
import com.niedu.repository.learning_record.UserAnswerRepository;
import com.niedu.service.edu.user_answer.strategy.UserAnswerMapperStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserAnswerMapperService {

    private final UserAnswerRepository userAnswerRepository;
    private final List<UserAnswerMapperStrategy> strategies;

    public AnswerResponse toResponse(StudiedStep studiedStep) {
        UserAnswer userAnswer = userAnswerRepository.findByStudiedStep(studiedStep);
        return strategies.stream()
                .filter(strategy -> strategy.supports(studiedStep.getStep().getType()))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("No UserAnswerMapper found for stepType: " + studiedStep.getStep().getType()))
                .toResponse(userAnswer);
    }

    public UserAnswer toEntity(StudiedStep studiedStep, AnswerResponse userAnswerRequest) {
        Step step = studiedStep.getStep();
        StepType stepType = step.getType();

        // 1. 기존 답변 조회
        UserAnswer existingUserAnswer = userAnswerRepository.findByStudiedStep(studiedStep);

        // 2. StepType에 맞는 Mapper 찾기
        UserAnswerMapperStrategy strategy = strategies.stream()
                .filter(s -> s.supports(stepType))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("No UserAnswerMapper found for stepType: " + stepType));

        // 3. 기존 답변 존재 여부에 따라 분기
        UserAnswer result;
        if (existingUserAnswer != null) {
            strategy.updateEntity(existingUserAnswer, userAnswerRequest);
            result = existingUserAnswer; // 수정된 객체 그대로 save
        } else {
            result = strategy.toEntity(studiedStep, userAnswerRequest);
        }

        // 4. 저장 및 반환
        return userAnswerRepository.save(result);
    }

    public boolean checkIsCorrect(StudiedStep studiedStep, AnswerResponse userAnswerRequest) {
        StepType stepType = studiedStep.getStep().getType();

        return strategies.stream()
                .filter(s -> s.supports(stepType))
                .findFirst()
                .map(s -> s.checkIsCorrect(studiedStep, userAnswerRequest))
                .orElseThrow(() ->
                        new IllegalArgumentException("No UserAnswerMapper found for stepType: " + stepType));
    }
}
