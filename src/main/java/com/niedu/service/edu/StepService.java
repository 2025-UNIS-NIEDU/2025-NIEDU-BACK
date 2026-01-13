package com.niedu.service.edu;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.niedu.dto.course.FeedbackAnswerRequest;
import com.niedu.dto.course.FeedbackAnswerResponse;
import com.niedu.dto.course.ShareAnswerRequest;
import com.niedu.dto.course.StepAnswerRequest;
import com.niedu.dto.course.user_answer.AnswerResponse;
import com.niedu.dto.course.user_answer.EmptyAnswerResponse;
import com.niedu.dto.course.user_answer.SentenceCompletionAnswerListResponse;
import com.niedu.dto.course.user_answer.SentenceCompletionAnswerResponse;
import com.niedu.dto.course.user_answer.SimpleAnswerListResponse;
import com.niedu.dto.course.user_answer.SimpleAnswerResponse;
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
import com.niedu.service.edu.user_answer.UserAnswerMapperService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StepService {
    private final UserAnswerMapperService userAnswerMapperService;
    private final AIService aiService;
    private final StudiedStepRepository studiedStepRepository;
    private final ContentRepository contentRepository;
    private final SharedResponseRepository sharedResponseRepository;
    private final AIErrorReportRepository aiErrorReportRepository;
    private final ObjectMapper objectMapper;

    public AnswerResponse submitStepAnswer(User user, Long stepId, StepAnswerRequest request) {
        // 1. UserAnswer entity 저장
        StudiedStep studiedStep = studiedStepRepository.findByUserAndStep_Id(user, stepId);
        StepType stepType = resolveStepType(studiedStep, request);

        if (request == null || request.userAnswer() == null || request.userAnswer().isNull()) {
            if (isNoAnswerStep(stepType)) {
                studiedStep.setIsCompleted(true);
                studiedStepRepository.save(studiedStep);
                return new EmptyAnswerResponse();
            }
            throw new IllegalArgumentException("userAnswer is required");
        }

        AnswerResponse userAnswer = mapUserAnswer(stepType, request.userAnswer());
        List<UserAnswer> userAnswers = userAnswerMapperService.toEntities(studiedStep, userAnswer);
        // 2. StudiedStep 업데이트
        studiedStep.setIsCompleted(true);
        studiedStepRepository.save(studiedStep);
        // 3. 리턴
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
        StepType actualType = studiedStep.getStep().getType();
        if (request != null && request.contentType() != null && !request.contentType().equals(actualType)) {
            throw new IllegalArgumentException("contentType mismatch: expected " + actualType + " but got " + request.contentType());
        }
        return actualType;
    }

    private AnswerResponse mapUserAnswer(StepType stepType, JsonNode userAnswerNode) {
        if (userAnswerNode == null || userAnswerNode.isNull()) {
            throw new IllegalArgumentException("userAnswer is required");
        }
        return switch (stepType) {
            case OX_QUIZ, MULTIPLE_CHOICE, SHORT_ANSWER ->
                    mapSimpleAnswer(userAnswerNode);
            case SENTENCE_COMPLETION ->
                    mapSentenceCompletionAnswer(userAnswerNode);
            case SUMMARY_READING ->
                    objectMapper.convertValue(userAnswerNode, SummaryReadingAnswerResponse.class);
            default ->
                    throw new IllegalArgumentException("Unsupported step type for answer: " + stepType);
        };
    }

    private boolean isNoAnswerStep(StepType stepType) {
        return stepType == StepType.TERM_LEARNING
                || stepType == StepType.CURRENT_AFFAIRS
                || stepType == StepType.ARTICLE_READING
                || stepType == StepType.SESSION_REFLECTION;
    }

    private AnswerResponse mapSimpleAnswer(JsonNode userAnswerNode) {
        if (userAnswerNode.has("answers")) {
            return objectMapper.convertValue(userAnswerNode, SimpleAnswerListResponse.class);
        }
        SimpleAnswerResponse single = objectMapper.convertValue(userAnswerNode, SimpleAnswerResponse.class);
        return new SimpleAnswerListResponse(List.of(single));
    }

    private AnswerResponse mapSentenceCompletionAnswer(JsonNode userAnswerNode) {
        if (userAnswerNode.has("answers")) {
            return objectMapper.convertValue(userAnswerNode, SentenceCompletionAnswerListResponse.class);
        }
        SentenceCompletionAnswerResponse single = objectMapper.convertValue(userAnswerNode, SentenceCompletionAnswerResponse.class);
        return new SentenceCompletionAnswerListResponse(List.of(single));
    }
}
