package com.niedu.service.edu.user_answer.strategy;

import com.niedu.dto.course.user_answer.AnswerResponse;
import com.niedu.dto.course.user_answer.SentenceCompletionAnswerResponse;
import com.niedu.entity.course.StepType;
import com.niedu.entity.learning_record.StudiedStep;
import com.niedu.entity.learning_record.user_answer.SentenceCompletionAnswer;
import com.niedu.entity.learning_record.user_answer.UserAnswer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SentenceCompletionAnswerMapper implements UserAnswerMapperStrategy{
    @Override
    public boolean supports(StepType type) {
        return type == StepType.SENTENCE_COMPLETION;
    }

    @Override
    public AnswerResponse toResponse(UserAnswer userAnswer) {
        if (userAnswer instanceof SentenceCompletionAnswer sentenceCompletionAnswer) {
            return new SentenceCompletionAnswerResponse(
                    sentenceCompletionAnswer.getQuestionIndex(),
                    sentenceCompletionAnswer.getUserAnswer(),
                    sentenceCompletionAnswer.getAIScore(),
                    sentenceCompletionAnswer.getAIFeedback()
            );
        }
        log.warn("지원되는 userAnswer이 아닙니다. type={}", userAnswer.getStudiedStep().getStep().getType());
        return null;
    }

    @Override
    public UserAnswer toEntity(StudiedStep studiedStep, AnswerResponse userAnswerRequest) {
        if (userAnswerRequest instanceof SentenceCompletionAnswerResponse sentenceCompletionAnswerResponse) {
            return SentenceCompletionAnswer.builder()
                    .isCorrect(checkIsCorrect(studiedStep, userAnswerRequest))
                    .studiedStep(studiedStep)
                    .questionIndex(sentenceCompletionAnswerResponse.questionIndex())
                    .userAnswer(sentenceCompletionAnswerResponse.userAnswer())
                    .AIScore(sentenceCompletionAnswerResponse.AIScore())
                    .AIFeedback(sentenceCompletionAnswerResponse.AIFeedback())
                    .build();
        }
        log.warn("지원되는 AnswerRequest가 아닙니다. type={}", studiedStep.getStep().getType());
        return null;
    }

    @Override
    public void updateEntity(UserAnswer existingUserAnswer, AnswerResponse userAnswerRequest) {
        if (existingUserAnswer instanceof SentenceCompletionAnswer entity
                && userAnswerRequest instanceof SentenceCompletionAnswerResponse request) {
            entity.setQuestionIndex(request.questionIndex());
            entity.setUserAnswer(request.userAnswer());
            entity.setAIScore(request.AIScore());
            entity.setAIFeedback(request.AIFeedback());
            entity.setIsCorrect(checkIsCorrect(entity.getStudiedStep(), userAnswerRequest));
        } else {
            log.warn("지원되는 AnswerRequest가 아닙니다. type={}", existingUserAnswer.getStudiedStep().getStep().getType());
        }
    }

    @Override
    public boolean checkIsCorrect(StudiedStep studiedStep, AnswerResponse request) {
        if (!(request instanceof SentenceCompletionAnswerResponse response)) {
            log.warn("지원되지 않는 AnswerResponse 타입: {}",
                    request != null ? request.getClass().getSimpleName() : "null");
            return false;
        }
        return response.AIScore() >= 80;
    }
}
