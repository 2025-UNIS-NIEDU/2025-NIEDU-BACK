package com.niedu.controller.edu;

import com.niedu.dto.course.*;
import com.niedu.dto.course.user_answer.AnswerResponse;
import com.niedu.entity.admin.AIErrorReport;
import com.niedu.entity.learning_record.SharedResponse;
import com.niedu.entity.user.User;
import com.niedu.global.response.ApiResponse;
import com.niedu.service.auth.AuthService;
import com.niedu.service.edu.StepService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/api/edu/courses/{courseId}/sessions/{sessionId}/steps")
@RequiredArgsConstructor
@Tag(name = "학습", description = "코스/세션/스텝 학습 관련 API")
public class StepController {
    private final StepService stepService;
    private final AuthService authService;

    @Operation(
            summary = "현재 STEP 사용자 응답 저장",
            description = "FUNCTION ID: EDU-QUIZ-GNB"
    )
    @PostMapping("/{stepId}/answer")
    public ResponseEntity<ApiResponse<?>> submitStepAnswer(HttpServletRequest httpServletRequest,
                                                           @PathVariable("courseId") Long courseId,
                                                           @PathVariable("sessionId") Long sessionId,
                                                           @PathVariable("stepId") Long stepId,
                                                           @RequestBody StepAnswerRequest request) {
        User user = authService.getUserFromRequest(httpServletRequest);
        AnswerResponse answerResponse = stepService.submitStepAnswer(user, stepId, request);
        return (answerResponse != null)?
                ResponseEntity.ok(ApiResponse.success(null)):
                ResponseEntity.internalServerError().body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "답변 저장에 실패했습니다."));
    }

    @Operation(
            summary = "세션 돌아보기 STEP - 내 답변 공유",
            description = "FUNCTION ID: EDU-QUIZ-07-IE-01"
    )
    @PostMapping("/{stepId}/share")
    public ResponseEntity<ApiResponse<?>> shareMyAnswer(HttpServletRequest httpServletRequest,
                                                        @PathVariable("courseId") Long courseId,
                                                        @PathVariable("sessionId") Long sessionId,
                                                        @PathVariable("stepId") Long stepId,
                                                        ShareAnswerRequest request) {
        User user = authService.getUserFromRequest(httpServletRequest);
        SharedResponse sharedResponse = stepService.shareMyAnswer(user, stepId, request);
        return (sharedResponse != null)?
                ResponseEntity.ok(ApiResponse.success(null)):
                ResponseEntity.internalServerError().body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "답변 저장에 실패했습니다."));
    }

    @Operation(
            summary = "세션 돌아보기 STEP - 타 유저 답변 확인",
            description = "FUNCTION ID: EDU-QUIZ-07-IE-02"
    )
    @GetMapping("/{stepId}/shared-answers")
    public ResponseEntity<ApiResponse<?>> getSharedAnswers(HttpServletRequest httpServletRequest,
                                                           @PathVariable("courseId") Long courseId,
                                                           @PathVariable("sessionId") Long sessionId,
                                                           @PathVariable("stepId") Long stepId) {
        User user = authService.getUserFromRequest(httpServletRequest);
        ArrayList<String> responses = stepService.getSharedAnswers(user, stepId);
        return (responses != null)?
                ResponseEntity.ok(ApiResponse.success(responses)):
                ResponseEntity.internalServerError().body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "공유된 답변 조회에 실패했습니다."));
    }

    @Operation(
            summary = "문장 완성 문제 STEP - 사용자 답안 제출 및 피드백 조회",
            description = "FUNCTION ID: EDU-QUIZ-09-E-01, EDU-QUIZ-09-E-02"
    )
    @PostMapping("{stepId}/submit-for-feedback")
    public ResponseEntity<ApiResponse<?>> submitStepAnswerForFeedback(HttpServletRequest httpServletRequest,
                                                                      @PathVariable("courseId") Long courseId,
                                                                      @PathVariable("sessionId") Long sessionId,
                                                                      @PathVariable("stepId") Long stepId,
                                                                      @RequestBody FeedbackAnswerRequest request) {
        User user = authService.getUserFromRequest(httpServletRequest);
        FeedbackAnswerResponse response = stepService.submitStepAnswerForFeedback(user, stepId, request);
        return (response != null)?
                ResponseEntity.ok(ApiResponse.success(response)):
                ResponseEntity.internalServerError().body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "AI 피드백 불러오기에 실패했습니다."));
    }

    @Operation(
            summary = "문장 완성 문제 STEP - AI 답변 오류 제보하기",
            description = "FUNCTION ID: EDU-QUIZ-09-E-03"
    )
    @PostMapping("/{stepId}/report")
    public ResponseEntity<ApiResponse<?>> reportErrorInFeedback(HttpServletRequest httpServletRequest,
                                                                @PathVariable("courseId") Long courseId,
                                                                @PathVariable("sessionId") Long sessionId,
                                                                @PathVariable("stepId") Long stepId,
                                                                @RequestBody ReportFeedbackRequest request) {
        User user = authService.getUserFromRequest(httpServletRequest);
        AIErrorReport aiErrorReport = stepService.reportErrorInFeedback(user, stepId, request.contentId());
        return (aiErrorReport != null)?
                ResponseEntity.ok(ApiResponse.success(null)):
                ResponseEntity.internalServerError().body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "AI 에러 제보에 실패했습니다."));
    }
}
