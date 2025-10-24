package com.niedu.service.edu.user_answer.strategy;

import com.niedu.dto.course.user_answer.AnswerResponse;
import com.niedu.dto.course.user_answer.SimpleAnswerResponse;
import com.niedu.entity.content.Content;
import com.niedu.entity.content.MultipleChoiceQuiz;
import com.niedu.entity.content.OxQuiz;
import com.niedu.entity.content.ShortAnswerQuiz;
import com.niedu.entity.course.Step;
import com.niedu.entity.course.StepType;
import com.niedu.entity.learning_record.StudiedStep;
import com.niedu.entity.learning_record.user_answer.SimpleAnswer;
import com.niedu.entity.learning_record.user_answer.UserAnswer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.Objects;

import static com.niedu.entity.course.StepType.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class SimpleAnswerMapper implements UserAnswerMapperStrategy{
    @Override
    public boolean supports(StepType type) {
        return EnumSet.of(OX_QUIZ, MULTIPLE_CHOICE, SHORT_ANSWER).contains(type);
    }

    @Override
    public AnswerResponse toResponse(UserAnswer userAnswer) {
        if (userAnswer instanceof SimpleAnswer simpleAnswer) {
            return new SimpleAnswerResponse(
                    simpleAnswer.getValue()
            );
        }
        log.warn("지원되는 userAnswer이 아닙니다. type={}", userAnswer.getStudiedStep().getStep().getType());
        return null;
    }

    @Override
    public UserAnswer toEntity(StudiedStep studiedStep, AnswerResponse userAnswerRequest) {
        if (userAnswerRequest instanceof SimpleAnswerResponse simpleAnswerResponse) {
            return SimpleAnswer.builder()
                    .isCorrect(checkIsCorrect(studiedStep, userAnswerRequest))
                    .studiedStep(studiedStep)
                    .value(simpleAnswerResponse.value())
                    .build();
        }
        log.warn("지원되는 AnswerRequest가 아닙니다. type={}", studiedStep.getStep().getType());
        return null;
    }

    @Override
    public void updateEntity(UserAnswer existingUserAnswer, AnswerResponse userAnswerRequest) {
        if (existingUserAnswer instanceof SimpleAnswer entity && userAnswerRequest instanceof SimpleAnswerResponse request) {
            entity.setValue(request.value());
            entity.setIsCorrect(checkIsCorrect(entity.getStudiedStep(), userAnswerRequest));
        }
        else {
            log.warn("지원되는 AnswerRequest가 아닙니다. type={}", existingUserAnswer.getStudiedStep().getStep().getType());
        }
    }

    @Override
    public boolean checkIsCorrect(StudiedStep studiedStep, AnswerResponse request) {
        // 타입 안전성 검사
        if (!(request instanceof SimpleAnswerResponse simpleAnswerResponse)) {
            log.warn("지원되지 않는 AnswerResponse 타입: {}",
                    request != null ? request.getClass().getSimpleName() : "null");
            return false;
        }

        Content content = studiedStep.getStep().getContent();
        if (content == null) {
            log.warn("StudiedStep {} 의 Content가 null입니다.", studiedStep.getId());
            return false;
        }

        // 정답 추출
        String correctAnswer = switch (content) {
            case OxQuiz ox -> ox.getCorrectAnswer();
            case MultipleChoiceQuiz mc -> mc.getCorrectAnswer();
            case ShortAnswerQuiz sa -> sa.getCorrectAnswer();
            default -> null;
        };

        // null-safe 비교
        return Objects.equals(correctAnswer, simpleAnswerResponse.value());
    }
}
