package com.niedu.service.edu.user_answer.strategy;

import com.niedu.dto.course.content.KeywordContent;
import com.niedu.dto.course.is_correct.IsCorrectResponse;
import com.niedu.dto.course.user_answer.AnswerResponse;
import com.niedu.dto.course.user_answer.SimpleAnswerResponse;
import com.niedu.dto.course.user_answer.SummaryReadingAnswerResponse;
import com.niedu.entity.content.Content;
import com.niedu.entity.content.SummaryReading;
import com.niedu.entity.course.StepType;
import com.niedu.entity.learning_record.StudiedStep;
import com.niedu.entity.learning_record.user_answer.SummaryReadingAnswer;
import com.niedu.entity.learning_record.user_answer.UserAnswer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import static com.niedu.entity.course.StepType.SUMMARY_READING;

@Component
@Slf4j
public class SummaryReadingAnswerMapper implements UserAnswerMapperStrategy{
    @Override
    public boolean supports(StepType type) {
        return type.equals(SUMMARY_READING);
    }

    @Override
    public AnswerResponse toResponse(List<UserAnswer> userAnswers) {
        if (userAnswers.get(0) instanceof SummaryReadingAnswer summaryReadingAnswer) {
            return new SummaryReadingAnswerResponse(
                    summaryReadingAnswer.getContent().getId(),
                    summaryReadingAnswer.getKeywords());
        }
        else throw new RuntimeException("response 변환 실패");
    }

    @Override
    public List<UserAnswer> toEntities(StudiedStep studiedStep, List<Content> contents, AnswerResponse request) {
        if (!(request instanceof SummaryReadingAnswerResponse summaryReadingAnswerResponse))
            throw new RuntimeException("entity 변환 실패");

        Map<Long, Content> contentMap = contents.stream()
                .collect(Collectors.toMap(Content::getId, c -> c));

        SummaryReadingAnswer summaryReadingAnswer = SummaryReadingAnswer.builder()
                .studiedStep(studiedStep)
                .content(contentMap.get(summaryReadingAnswerResponse.contentId()))
                .keywords(summaryReadingAnswerResponse.keywords())
                .build();

        return List.of(summaryReadingAnswer);
    }

    @Override
    public void updateEntities(List<UserAnswer> existingUserAnswers, List<Content> contents, AnswerResponse request) {
        if (!(request instanceof SummaryReadingAnswerResponse summaryReadingAnswerResponse)) {
            throw new RuntimeException("entity 업데이트 실패");
        }

        // contentId → Content 매핑
        Map<Long, Content> contentMap = contents.stream()
                .collect(Collectors.toMap(Content::getId, c -> c));

        Long contentId = summaryReadingAnswerResponse.contentId();
        Content content = contentMap.get(contentId);
        if (content == null) {
            throw new RuntimeException("해당 contentId에 대응되는 Content를 찾을 수 없습니다: " + contentId);
        }

        existingUserAnswers.stream()
                .filter(answer -> answer instanceof SummaryReadingAnswer)
                .map(answer -> (SummaryReadingAnswer) answer)
                .filter(answer -> answer.getContent() != null && answer.getContent().getId().equals(contentId))
                .forEach(entity -> {
                    entity.setContent(content);
                    entity.setKeywords(summaryReadingAnswerResponse.keywords());
                });
    }

    @Override
    public List<IsCorrectResponse> checkIsCorrect(List<Content> contents, AnswerResponse request) {
        if (request instanceof SummaryReadingAnswerResponse summaryReadingAnswerResponse) {
            Set<String> correctAnswerSet = ((SummaryReading) contents.get(0)).getKeywords().stream()
                    .filter(KeywordContent::isTopicWord)
                    .map(KeywordContent::word)
                    .map(String::toLowerCase)
                    .collect(Collectors.toSet());

            Set<String> userAnswerSet = summaryReadingAnswerResponse.keywords().stream()
                    .map(String::toLowerCase)
                    .collect(Collectors.toSet());

            boolean isCorrect = userAnswerSet.containsAll(correctAnswerSet)
                    && correctAnswerSet.containsAll(userAnswerSet);

            return List.of(new IsCorrectResponse(summaryReadingAnswerResponse.contentId(), isCorrect));
        }
        else throw new RuntimeException("정답 여부 조회 실패");
    }
}
