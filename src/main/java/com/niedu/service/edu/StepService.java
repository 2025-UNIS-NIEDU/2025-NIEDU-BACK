package com.niedu.service.edu;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.niedu.dto.course.*;
import com.niedu.dto.course.user_answer.AnswerResponse;
import com.niedu.dto.course.user_answer.SentenceCompletionAnswerListResponse;
import com.niedu.dto.course.user_answer.SimpleAnswerListResponse;
import com.niedu.dto.course.user_answer.SummaryReadingAnswerResponse;
import com.niedu.entity.admin.AIErrorReport;
import com.niedu.entity.course.StepType;
import com.niedu.entity.learning_record.SharedResponse;
import com.niedu.entity.learning_record.StudiedStep;
import com.niedu.entity.learning_record.user_answer.UserAnswer;
import com.niedu.entity.user.User;
import com.niedu.repository.admin.AIErrorReportRepository;
import com.niedu.repository.content.ContentRepository;
import com.niedu.repository.learning_record.SharedResponseRepository;
import com.niedu.repository.learning_record.StudiedStepRepository;
import com.niedu.repository.course.StepRepository;
import com.niedu.service.edu.user_answer.UserAnswerMapperService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StepService {
    private final UserAnswerMapperService userAnswerMapperService;
    private final AIService aiService;
    private final StudiedStepRepository studiedStepRepository;
    private final StepRepository stepRepository;
    private final ContentRepository contentRepository;
    private final SharedResponseRepository sharedResponseRepository;
    private final AIErrorReportRepository aiErrorReportRepository;
    private final ObjectMapper objectMapper;

    public AnswerResponse submitStepAnswer(User user, Long stepId, StepAnswerRequest request) {
        // 1. StudiedStep 확보 (세션 시작 없이 답안이 들어오는 경우 대비)
        StudiedStep studiedStep = studiedStepRepository.findByUserAndStep_Id(user, stepId);
        if (studiedStep == null) {
            Step step = stepRepository.findById(stepId)
                    .orElseThrow(() -> new IllegalArgumentException("Step not found: " + stepId));
            studiedStep = StudiedStep.builder()
                    .user(user)
                    .step(step)
                    .isCompleted(false)
                    .build();
            studiedStep = studiedStepRepository.save(studiedStep);
        }
        // 2. UserAnswer entity 저장
        StepType stepType = resolveStepType(studiedStep, request);
        AnswerResponse userAnswer = mapUserAnswer(stepType, request.userAnswer());
        List<UserAnswer> userAnswers = userAnswerMapperService.toEntities(studiedStep, userAnswer);
        // 3. StudiedStep 업데이트
        studiedStep.setIsCompleted(true);
        studiedStepRepository.save(studiedStep);
        // 4. 리턴
        return userAnswerMapperService.toResponse(studiedStep);
    }

    public SharedResponse shareMyAnswer(User user, Long stepId, ShareAnswerRequest request) {
        StudiedStep studiedStep = studiedStepRepository.findByUserAndStep_Id(user, stepId);
        SharedResponse sharedResponse = new SharedResponse(
                null,
                user,
                studiedStep.getStep(),
                request.userAnswer(),
                LocalDateTime.now()
        );
        SharedResponse saved = sharedResponseRepository.save(sharedResponse);
        return saved;
    }

    public ArrayList<String> getSharedAnswers(User user, Long stepId) {
        StudiedStep studiedStep = studiedStepRepository.findByUserAndStep_Id(user, stepId);
        List<SharedResponse> sharedResponses = sharedResponseRepository.findAllByStepAndUserNot(studiedStep.getStep(), user);
        ArrayList<String> responses = sharedResponses.stream()
                .map(SharedResponse::getUserResponse)
                .collect(Collectors.toCollection(ArrayList::new));
        return responses;
    }

    public FeedbackAnswerResponse submitStepAnswerForFeedback(User user, Long stepId, FeedbackAnswerRequest request) {
        String referenceAnswer = contentRepository.findSentenceCompletionReferenceAnswer(stepId, request.contentId())
                .orElseThrow(() -> new IllegalArgumentException("Reference answer not found for contentId: " + request.contentId()));
        FeedbackAnswerResponse response = aiService.submitStepAnswerForFeedback(
                request.contentId(),
                request.userAnswer(),
                referenceAnswer
        );
        return response;
    }

    public AIErrorReport reportErrorInFeedback(User user, Long stepId, Long contentId) {
        StudiedStep studiedStep = studiedStepRepository.findByUserAndStep_Id(user, stepId);
        AIErrorReport aiErrorReport = new AIErrorReport(
                null,
                studiedStep.getStep(),
                contentId);
        AIErrorReport saved = aiErrorReportRepository.save(aiErrorReport);
        return saved;
    }

    private StepType resolveStepType(StudiedStep studiedStep, StepAnswerRequest request) {
        if (request != null && request.contentType() != null) {
            return request.contentType();
        }
        return studiedStep.getStep().getType();
    }

    private AnswerResponse mapUserAnswer(StepType stepType, JsonNode userAnswerNode) {
        if (userAnswerNode == null || userAnswerNode.isNull()) {
            throw new IllegalArgumentException("userAnswer is required");
        }
        return switch (stepType) {
            case OX_QUIZ, MULTIPLE_CHOICE, SHORT_ANSWER ->
                    objectMapper.convertValue(userAnswerNode, SimpleAnswerListResponse.class);
            case SENTENCE_COMPLETION ->
                    objectMapper.convertValue(userAnswerNode, SentenceCompletionAnswerListResponse.class);
            case SUMMARY_READING ->
                    objectMapper.convertValue(userAnswerNode, SummaryReadingAnswerResponse.class);
            default ->
                    throw new IllegalArgumentException("Unsupported step type for answer: " + stepType);
        };
    }
}
