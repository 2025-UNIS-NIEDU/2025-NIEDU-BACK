package com.niedu.service.edu.user_answer;

import com.niedu.dto.course.is_correct.IsCorrectResponse;
import com.niedu.dto.course.user_answer.AnswerResponse;
import com.niedu.entity.content.Content;
import com.niedu.entity.course.Step;
import com.niedu.entity.course.StepType;
import com.niedu.entity.learning_record.StudiedStep;
import com.niedu.entity.learning_record.user_answer.UserAnswer;
import com.niedu.repository.content.ContentRepository;
import com.niedu.repository.learning_record.UserAnswerRepository;
import com.niedu.service.edu.user_answer.strategy.UserAnswerMapperStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserAnswerMapperService {

    private final ContentRepository contentRepository;
    private final UserAnswerRepository userAnswerRepository;
    private final List<UserAnswerMapperStrategy> strategies;

    // record 변환
    public AnswerResponse toResponse(StudiedStep studiedStep) {
        List<UserAnswer> userAnswers = userAnswerRepository.findAllByStudiedStep(studiedStep);
        if (userAnswers.isEmpty()) return null;

        StepType stepType = studiedStep.getStep().getType();
        UserAnswerMapperStrategy strategy = findStrategy(stepType);

        return strategy.toResponse(userAnswers);
    }

    // 엔티티 생성 또는 업데이트
    public List<UserAnswer> toEntities(StudiedStep studiedStep, AnswerResponse userAnswerRequest) {
        StepType stepType = studiedStep.getStep().getType();
        UserAnswerMapperStrategy strategy = findStrategy(stepType);

        List<UserAnswer> existing = userAnswerRepository.findAllByStudiedStep(studiedStep);
        List<Content> contents = contentRepository.findAllByStep(studiedStep.getStep());

        List<UserAnswer> result;
        if (!existing.isEmpty()) {
            strategy.updateEntities(existing, contents, userAnswerRequest);
            result = existing;
        } else {
            result = strategy.toEntities(studiedStep, contents, userAnswerRequest);
        }

        List<IsCorrectResponse> isCorrectResponses = strategy.checkIsCorrect(contents, userAnswerRequest);
        Map<Long, Boolean> isCorrectMap = (isCorrectResponses == null) ? Map.of() :
                isCorrectResponses.stream()
                        .filter(resp -> resp.contentId() != null)
                        .collect(Collectors.toMap(IsCorrectResponse::contentId, IsCorrectResponse::isCorrect, (a, b) -> a));

        result.forEach(answer -> {
            Content content = answer.getContent();
            if (content == null) return;
            Boolean isCorrect = isCorrectMap.get(content.getId());
            if (isCorrect != null) {
                answer.setIsCorrect(isCorrect);
            }
        });

        userAnswerRepository.saveAll(result);
        return result;
    }

    // 정답 여부 판단
    public List<IsCorrectResponse> checkIsCorrect(StudiedStep studiedStep) {
        List<UserAnswer> userAnswers = userAnswerRepository.findAllByStudiedStep(studiedStep);
        if (userAnswers.isEmpty()) return List.of();

        List<Content> contents = contentRepository.findAllByStep(studiedStep.getStep());
        if (contents.isEmpty()) return List.of();

        AnswerResponse userAnswerResponse = toResponse(studiedStep);
        if (userAnswerResponse == null) return List.of();

        StepType stepType = studiedStep.getStep().getType();
        UserAnswerMapperStrategy strategy = findStrategy(stepType);

        return strategy.checkIsCorrect(contents, userAnswerResponse);
    }

    private UserAnswerMapperStrategy findStrategy(StepType stepType) {
        return strategies.stream()
                .filter(s -> s.supports(stepType))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No strategy for type: " + stepType));
    }
}
