package com.niedu.service.edu.user_answer.strategy;

import com.niedu.dto.course.content.KeywordContent;
import com.niedu.dto.course.user_answer.AnswerResponse;
import com.niedu.dto.course.user_answer.SummaryReadingAnswerResponse;
import com.niedu.entity.content.Content;
import com.niedu.entity.content.SummaryReading;
import com.niedu.entity.course.StepType;
import com.niedu.entity.learning_record.StudiedStep;
import com.niedu.entity.learning_record.user_answer.SummaryReadingAnswer;
import com.niedu.entity.learning_record.user_answer.UserAnswer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@Slf4j
public class SummaryReadingAnswerMapper implements UserAnswerMapperStrategy{
    @Override
    public boolean supports(StepType type) {
        return type == StepType.SUMMARY_READING;
    }

    @Override
    public AnswerResponse toResponse(UserAnswer userAnswer) {
        if (userAnswer instanceof SummaryReadingAnswer summaryReadingAnswer) {
            return new SummaryReadingAnswerResponse(
                    summaryReadingAnswer.getKeywords()
            );
        }
        log.warn("지원되는 userAnswer이 아닙니다. type={}", userAnswer.getStudiedStep().getStep().getType());
        return null;
    }

    @Override
    public UserAnswer toEntity(StudiedStep studiedStep, AnswerResponse userAnswerRequest) {
        if (userAnswerRequest instanceof SummaryReadingAnswerResponse summaryReadingAnswerResponse) {
            return SummaryReadingAnswer.builder()
                    .studiedStep(studiedStep)
                    .keywords(summaryReadingAnswerResponse.keywords())
                    .isCorrect(checkIsCorrect(studiedStep, userAnswerRequest))
                    .build();
        }
        log.warn("지원되는 AnswerRequest가 아닙니다. type={}", studiedStep.getStep().getType());
        return null;
    }

    @Override
    public void updateEntity(UserAnswer existingUserAnswer, AnswerResponse userAnswerRequest) {
        if (existingUserAnswer instanceof SummaryReadingAnswer entity && userAnswerRequest instanceof SummaryReadingAnswerResponse request) {
            entity.setKeywords(request.keywords());
            entity.setIsCorrect(checkIsCorrect(entity.getStudiedStep(), userAnswerRequest));
        }
        else {
            log.warn("지원되는 AnswerRequest가 아닙니다. type={}", existingUserAnswer.getStudiedStep().getStep().getType());
        }
    }

    @Override
    public boolean checkIsCorrect(StudiedStep studiedStep, AnswerResponse request) {
        if (!(request instanceof SummaryReadingAnswerResponse summaryReadingAnswerResponse)) {
            log.warn("지원되지 않는 AnswerResponse 타입: {}", request.getClass().getSimpleName());
            return false;
        }

        Content content = studiedStep.getStep().getContent();
        if (!(content instanceof SummaryReading summaryReading)) {
            log.warn("StudiedStep {} 의 Content가 SummaryReading이 아닙니다.", studiedStep.getId());
            return false;
        }

        List<String> correctKeywords = summaryReading.getKeywords().stream()
                .filter(KeywordContent::isTopicWord)
                .map(KeywordContent::word)
                .filter(Objects::nonNull)
                .map(String::trim)
                .toList();

        List<String> userKeywords = summaryReadingAnswerResponse.keywords();
        if (userKeywords == null || userKeywords.isEmpty()) return false;

        return userKeywords.containsAll(correctKeywords);
    }
}
