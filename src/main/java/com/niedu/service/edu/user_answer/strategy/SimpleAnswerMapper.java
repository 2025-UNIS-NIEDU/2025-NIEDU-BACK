package com.niedu.service.edu.user_answer.strategy;

import com.niedu.dto.course.is_correct.IsCorrectResponse;
import com.niedu.dto.course.user_answer.AnswerResponse;
import com.niedu.dto.course.user_answer.SentenceCompletionAnswerResponse;
import com.niedu.dto.course.user_answer.SimpleAnswerListResponse;
import com.niedu.dto.course.user_answer.SimpleAnswerResponse;
import com.niedu.entity.content.Content;
import com.niedu.entity.content.MultipleChoiceQuiz;
import com.niedu.entity.content.OxQuiz;
import com.niedu.entity.content.ShortAnswerQuiz;
import com.niedu.entity.course.StepType;
import com.niedu.entity.learning_record.StudiedStep;
import com.niedu.entity.learning_record.user_answer.SimpleAnswer;
import com.niedu.entity.learning_record.user_answer.UserAnswer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.niedu.entity.course.StepType.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class SimpleAnswerMapper implements UserAnswerMapperStrategy{
    @Override
    public boolean supports(StepType type) {
        if (type.equals(SHORT_ANSWER) || type.equals(OX_QUIZ) || type.equals(MULTIPLE_CHOICE)) return true;
        return false;
    }

    @Override
    public AnswerResponse toResponse(List<UserAnswer> userAnswers) {
        List<SimpleAnswerResponse> simpleAnswerResponses = userAnswers.stream()
                .filter(userAnswer -> userAnswer instanceof SimpleAnswer)
                .map(userAnswer -> {
                    SimpleAnswer simpleAnswer = (SimpleAnswer) userAnswer;
                    return new SimpleAnswerResponse(
                            simpleAnswer.getContent().getId(),
                            simpleAnswer.getValue()
                    );
                })
                .toList();
        if (simpleAnswerResponses == null || simpleAnswerResponses.isEmpty())
            throw new RuntimeException("response 변환 실패");
        return new SimpleAnswerListResponse(simpleAnswerResponses);
    }

    @Override
    public List<UserAnswer> toEntities(StudiedStep studiedStep, List<Content> contents, AnswerResponse request) {
        if (!(request instanceof SimpleAnswerListResponse listResponse))
            throw new RuntimeException("entity 변환 실패");

        Map<Long, Content> contentMap = contents.stream()
                .collect(Collectors.toMap(Content::getId, c -> c));


        return listResponse.answers().stream()
                .map(resp -> {
                    Content content = contentMap.get(resp.contentId());
                    if (content == null) {
                        log.warn("No matching content found for contentId={}", resp.contentId());
                    }
                    return SimpleAnswer.builder()
                            .studiedStep(studiedStep)
                            .content(content)
                            .value(resp.value())
                            .build();
                })
                .map(answer -> (UserAnswer) answer)
                .toList();
    }

    @Override
    public void updateEntities(List<UserAnswer> existingUserAnswers, List<Content> contents, AnswerResponse request) {
        if (!(request instanceof SimpleAnswerListResponse listResponse)) {
            throw new RuntimeException("Invalid request type for SENTENCE_COMPLETION");
        }

        // contentId → Content 매핑
        Map<Long, Content> contentMap = contents.stream()
                .collect(Collectors.toMap(Content::getId, c -> c));

        // contentId → AnswerResponse 매핑
        Map<Long, SimpleAnswerResponse> responseMap = listResponse.answers().stream()
                .collect(Collectors.toMap(SimpleAnswerResponse::contentId, r -> r));

        existingUserAnswers.stream()
                .filter(userAnswer -> userAnswer instanceof SimpleAnswer)
                .map(userAnswer -> (SimpleAnswer) userAnswer)
                .forEach(entity -> {
                    Long contentId = entity.getContent().getId();
                    SimpleAnswerResponse newResponse = responseMap.get(contentId);
                    if (newResponse == null) {
                        log.warn("No matching response for contentId={}", contentId);
                        return;
                    }

                    // 업데이트 필드들
                    entity.setValue(newResponse.value());
                });
    }

    @Override
    public List<IsCorrectResponse> checkIsCorrect(List<Content> contents, AnswerResponse request) {
        Map<Long, String> contentAnswerMap = contents.stream()
                .filter(content ->
                        content instanceof MultipleChoiceQuiz ||
                                content instanceof OxQuiz ||
                                content instanceof ShortAnswerQuiz)
                .collect(Collectors.toMap(Content::getId, content -> {
                    if (content instanceof MultipleChoiceQuiz quiz) {
                        return quiz.getCorrectAnswer();
                    } else if (content instanceof OxQuiz quiz) {
                        return quiz.getCorrectAnswer();
                    } else if (content instanceof ShortAnswerQuiz quiz) {
                        return quiz.getCorrectAnswer();
                    }
                    return null;
                }));

        if (request instanceof SimpleAnswerListResponse simpleAnswerListResponse) {
            List<SimpleAnswerResponse> simpleAnswerResponses = simpleAnswerListResponse.answers();
            List<IsCorrectResponse> isCorrectResponses = simpleAnswerResponses.stream()
                    .map(simpleAnswerResponse -> {
                        String correctAnswer = contentAnswerMap.get(simpleAnswerResponse.contentId());
                        return new IsCorrectResponse(simpleAnswerResponse.contentId(), (correctAnswer.equals(simpleAnswerResponse.value())));
                    })
                    .toList();
            return isCorrectResponses;
        }
        else throw new RuntimeException("정답 여부 조회 실패");
    }

}
