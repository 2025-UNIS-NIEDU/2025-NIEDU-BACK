package com.niedu.service.edu.user_answer.strategy;

import com.niedu.dto.course.is_correct.IsCorrectResponse;
import com.niedu.dto.course.user_answer.AnswerResponse;
import com.niedu.dto.course.user_answer.SentenceCompletionAnswerListResponse;
import com.niedu.dto.course.user_answer.SentenceCompletionAnswerResponse;
import com.niedu.entity.content.Content;
import com.niedu.entity.content.SentenceCompletionQuiz;
import com.niedu.entity.course.StepType;
import com.niedu.entity.learning_record.StudiedStep;
import com.niedu.entity.learning_record.user_answer.SentenceCompletionAnswer;
import com.niedu.entity.learning_record.user_answer.UserAnswer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.niedu.entity.course.StepType.SENTENCE_COMPLETION;

@Component
@Slf4j
public class SentenceCompletionAnswerMapper implements UserAnswerMapperStrategy{
    @Override
    public boolean supports(StepType type) {
        if (type.equals(SENTENCE_COMPLETION)) return true;
        return false;
    }

    @Override
    public AnswerResponse toResponse(List<UserAnswer> userAnswers) {
        List<SentenceCompletionAnswerResponse> sentenceCompletionAnswerResponses = userAnswers.stream()
                .filter(userAnswer -> userAnswer instanceof SentenceCompletionAnswer)
                .map(userAnswer -> {
                    SentenceCompletionAnswer sentenceCompletionAnswer = (SentenceCompletionAnswer) userAnswer;
                    return new SentenceCompletionAnswerResponse(
                            sentenceCompletionAnswer.getContent().getId(),
                            sentenceCompletionAnswer.getUserAnswer(),
                            sentenceCompletionAnswer.getAIScore(),
                            sentenceCompletionAnswer.getAIFeedback()
                    );
                })
                .toList();
        if (sentenceCompletionAnswerResponses == null || sentenceCompletionAnswerResponses.isEmpty())
            throw new RuntimeException("response 변환 실패");
        return new SentenceCompletionAnswerListResponse(sentenceCompletionAnswerResponses);
    }

    @Override
    public List<UserAnswer> toEntities(StudiedStep studiedStep, List<Content> contents, AnswerResponse request) {
        if (!(request instanceof SentenceCompletionAnswerListResponse listResponse)) {
            throw new RuntimeException("entity 변환 실패");
        }

        // contentId -> Content 매핑용 map 생성
        Map<Long, Content> contentMap = contents.stream()
                .collect(Collectors.toMap(Content::getId, c -> c));

        return listResponse.answers().stream()
                .map(resp -> {
                    Content content = contentMap.get(resp.contentId());
                    if (content == null) {
                        log.warn("No matching content found for contentId={}", resp.contentId());
                    }
                    return SentenceCompletionAnswer.builder()
                            .studiedStep(studiedStep)
                            .content(content)
                            .userAnswer(resp.userAnswer())
                            .AIScore(resp.AIScore())
                            .AIFeedback(resp.AIFeedback())
                            .build();
                })
                .map(answer -> (UserAnswer) answer)
                .toList();
    }

    @Override
    public void updateEntities(List<UserAnswer> existingUserAnswers, List<Content> contents, AnswerResponse request) {
        if (!(request instanceof SentenceCompletionAnswerListResponse listResponse)) {
            throw new RuntimeException("entity 업데이트 실패");
        }

        // contentId → Content 매핑
        Map<Long, Content> contentMap = contents.stream()
                .collect(Collectors.toMap(Content::getId, c -> c));

        // contentId → AnswerResponse 매핑
        Map<Long, SentenceCompletionAnswerResponse> responseMap = listResponse.answers().stream()
                .collect(Collectors.toMap(SentenceCompletionAnswerResponse::contentId, r -> r));

        existingUserAnswers.stream()
                .filter(userAnswer -> userAnswer instanceof SentenceCompletionAnswer)
                .map(userAnswer -> (SentenceCompletionAnswer) userAnswer)
                .forEach(entity -> {
                    Long contentId = entity.getContent().getId();
                    SentenceCompletionAnswerResponse newResponse = responseMap.get(contentId);
                    if (newResponse == null) {
                        log.warn("No matching response for contentId={}", contentId);
                        return;
                    }

                    // 업데이트 필드들
                    entity.setUserAnswer(newResponse.userAnswer());
                    entity.setAIScore(newResponse.AIScore());
                    entity.setAIFeedback(newResponse.AIFeedback());
                });
    }
    @Override
    public List<IsCorrectResponse> checkIsCorrect(List<Content> contents, AnswerResponse request) {
        // AI 서버 완성되면 채울 예정
        return null;
    }

}
