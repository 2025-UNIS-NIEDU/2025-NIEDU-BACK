package com.niedu.service.edu;

import com.niedu.dto.course.*;
import com.niedu.dto.course.user_answer.AnswerResponse;
import com.niedu.entity.admin.AIErrorReport;
import com.niedu.entity.learning_record.SharedResponse;
import com.niedu.entity.learning_record.StudiedStep;
import com.niedu.entity.learning_record.user_answer.UserAnswer;
import com.niedu.entity.user.User;
import com.niedu.repository.admin.AIErrorReportRepository;
import com.niedu.repository.learning_record.SharedResponseRepository;
import com.niedu.repository.learning_record.StudiedStepRepository;
import com.niedu.repository.learning_record.UserAnswerRepository;
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
    private final UserAnswerRepository userAnswerRepository;
    private final SharedResponseRepository sharedResponseRepository;
    private final AIErrorReportRepository aiErrorReportRepository;

    public AnswerResponse submitStepAnswer(User user, Long stepId, StepAnswerRequest request) {
        // 1. UserAnswer entity 저장
        StudiedStep studiedStep = studiedStepRepository.findByUser_IdAndStep_Id(user.getId(), stepId);
        List<UserAnswer> userAnswers = userAnswerMapperService.toEntities(studiedStep, request.userAnswer());
        // 2. StudiedStep 업데이트
        studiedStep.setIsCompleted(true);
        // 3. 리턴
        return userAnswerMapperService.toResponse(studiedStep);
    }

    public SharedResponse shareMyAnswer(User user, Long stepId, ShareAnswerRequest request) {
        StudiedStep studiedStep = studiedStepRepository.findByUser_IdAndStep_Id(user.getId(), stepId);
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
        StudiedStep studiedStep = studiedStepRepository.findByUser_IdAndStep_Id(user.getId(), stepId);
        List<SharedResponse> sharedResponses = sharedResponseRepository.findAllByStepAndUserNot(studiedStep.getStep(), user);
        ArrayList<String> responses = sharedResponses.stream()
                .map(SharedResponse::getUserResponse)
                .collect(Collectors.toCollection(ArrayList::new));
        return responses;
    }

    public FeedbackAnswerResponse submitStepAnswerForFeedback(User user, Long stepId, FeedbackAnswerRequest request) {
        FeedbackAnswerResponse response = aiService.submitStepAnswerForFeedback(user, stepId, request);
        return response;
    }

    public AIErrorReport reportErrorInFeedback(User user, Long stepId, ReportFeedbackRequest request) {
        StudiedStep studiedStep = studiedStepRepository.findByUser_IdAndStep_Id(user.getId(), stepId);
        AIErrorReport aiErrorReport = new AIErrorReport(
                null,
                studiedStep.getStep(),
                request.questionNum());
        AIErrorReport saved = aiErrorReportRepository.save(aiErrorReport);
        return saved;
    }
}
