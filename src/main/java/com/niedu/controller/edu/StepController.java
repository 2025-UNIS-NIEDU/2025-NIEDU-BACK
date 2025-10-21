package com.niedu.controller.edu;

import com.niedu.dto.course.*;
import com.niedu.entity.admin.AIErrorReport;
import com.niedu.entity.learning_record.SharedResponse;
import com.niedu.entity.learning_record.quiz_response.QuizResponse;
import com.niedu.entity.user.User;
import com.niedu.global.response.ApiResponse;
import com.niedu.service.auth.AuthService;
import com.niedu.service.edu.StepService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/api/edu/courses/{courseId}/sessions/{sessionId}/steps")
@RequiredArgsConstructor
public class StepController {
    private final StepService stepService;
    private final AuthService authService;

    @PostMapping("/{stepId}/answer")
    public ResponseEntity<ApiResponse<?>> submitStepAnswer(HttpServletRequest httpServletRequest,
                                                           @PathVariable("courseId") Long courseId,
                                                           @PathVariable("sessionId") Long sessionId,
                                                           @PathVariable("stepId") Long stepId,
                                                           @RequestBody StepAnswerRequest request) {
        User user = authService.getUserFromRequest(httpServletRequest);
        QuizResponse quizResponse = stepService.submitStepAnswer(user, courseId, sessionId, request);
        return (quizResponse != null)?
                ResponseEntity.ok(ApiResponse.success(null)):
                ResponseEntity.internalServerError().body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "답변 저장에 실패했습니다."));
    }

    @PostMapping("/{stepId}/share")
    public ResponseEntity<ApiResponse<?>> shareMyAnswer(HttpServletRequest httpServletRequest,
                                                        @PathVariable("courseId") Long courseId,
                                                        @PathVariable("sessionId") Long sessionId,
                                                        @PathVariable("stepId") Long stepId,
                                                        ShareAnswerRequest request) {
        User user = authService.getUserFromRequest(httpServletRequest);
        SharedResponse sharedResponse = stepService.shareMyAnswer(user, courseId, sessionId, stepId);
        return (sharedResponse != null)?
                ResponseEntity.ok(ApiResponse.success(null)):
                ResponseEntity.internalServerError().body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "답변 저장에 실패했습니다."));
    }

    @GetMapping("/{stepId}/shared-answers")
    public ResponseEntity<ApiResponse<?>> getSharedAnswers(HttpServletRequest httpServletRequest,
                                                           @PathVariable("courseId") Long courseId,
                                                           @PathVariable("sessionId") Long sessionId,
                                                           @PathVariable("stepId") Long stepId) {
        User user = authService.getUserFromRequest(httpServletRequest);
        ArrayList<String> responses = stepService.getSharedAnswers(user, courseId, sessionId, stepId);
        return (responses != null)?
                ResponseEntity.ok(ApiResponse.success(responses)):
                ResponseEntity.internalServerError().body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "공유된 답변 조회에 실패했습니다."));
    }

    @PostMapping("{stepId}/submit-for-feedback")
    public ResponseEntity<ApiResponse<?>> submitStepAnswerForFeedback(HttpServletRequest httpServletRequest,
                                                                      @PathVariable("courseId") Long courseId,
                                                                      @PathVariable("sessionId") Long sessionId,
                                                                      @PathVariable("stepId") Long stepId,
                                                                      @RequestBody FeedbackAnswerRequest request) {
        User user = authService.getUserFromRequest(httpServletRequest);
        FeedbackAnswerResponse response = stepService.submitStepAnswerForFeedback(user, courseId, sessionId, stepId, request);
        return (response != null)?
                ResponseEntity.ok(ApiResponse.success(response)):
                ResponseEntity.internalServerError().body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "AI 피드백 불러오기에 실패했습니다."));
    }

    @PostMapping("/{stepId}/report")
    public ResponseEntity<ApiResponse<?>> reportErrorInFeedback(HttpServletRequest httpServletRequest,
                                                                @PathVariable("courseId") Long courseId,
                                                                @PathVariable("sessionId") Long sessionId,
                                                                @PathVariable("stepId") Long stepId,
                                                                @RequestBody ReportFeedbackRequest request) {
        User user = authService.getUserFromRequest(httpServletRequest);
        AIErrorReport aiErrorReport = stepService.reportErrorInFeedback(user, courseId, sessionId, stepId, request);
        return (aiErrorReport != null)?
                ResponseEntity.ok(ApiResponse.success(null)):
                ResponseEntity.internalServerError().body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "AI 에러 제보에 실패했습니다."));
    }
}
