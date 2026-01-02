package com.niedu.controller.edu;

import com.niedu.dto.course.LevelRequest;
import com.niedu.dto.course.SessionListResponse;
import com.niedu.dto.course.SessionStartResponse;
import com.niedu.dto.course.SessionSummaryResponse;
import com.niedu.entity.user.User;
import com.niedu.global.response.ApiResponse;
import com.niedu.service.auth.AuthService;
import com.niedu.service.edu.SessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/api/edu/courses/{courseId}/sessions")
@RequiredArgsConstructor
@Tag(name = "학습", description = "코스/세션/스텝 학습 관련 API")
public class SessionController {
    private final SessionService sessionService;
    private final AuthService authService;

    @Operation(
            summary = "특정 코스 내 세션 리스트 조회",
            description = "FUNCTION ID: EDU-DETAIL-05"
    )
    @GetMapping("")
    public ResponseEntity<ApiResponse<?>> getSessions(@PathVariable("courseId") Long courseId) {
        ArrayList<SessionListResponse> responses = sessionService.getSessions(courseId);
        return (responses != null)?
                ResponseEntity.ok(ApiResponse.success(responses)):
                ResponseEntity.internalServerError().body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "세션 목록 조회에 실패했습니다."));
    }

    @Operation(
            summary = "특정 코스 내 특정 세션 시작 (step 메타데이터/진입점 step 정보)",
            description = "FUNCTION ID: EDU-QUIZ-STEP-03"
    )
    @PostMapping("/{sessionId}/start")
    public ResponseEntity<ApiResponse<?>> startSession(HttpServletRequest httpServletRequest,
                                                       @PathVariable("sessionId") Long sessionId,
                                                       @RequestBody LevelRequest request) {
        User user = authService.getUserFromRequest(httpServletRequest);
        SessionStartResponse response = sessionService.startSession(user, sessionId, request);
        return (response != null)?
                ResponseEntity.ok(ApiResponse.success(response)):
                ResponseEntity.internalServerError().body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "세션 시작에 실패했습니다."));
    }

    @Operation(
            summary = "학습 세션 종료",
            description = "FUNCTION ID: EDU-QUIZ-RESULT-02, EDU-QUIZ-QUIT"
    )
    @PostMapping("/{sessionId}/quit")
    public ResponseEntity<ApiResponse<?>> quitSession(HttpServletRequest httpServletRequest,
                                                       @PathVariable("sessionId") Long sessionId) {
        User user = authService.getUserFromRequest(httpServletRequest);
        sessionService.quitSession(user, sessionId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(
            summary = "학습 세션 마무리",
            description = "FUNCTION ID: EDU-QUIZ-RESULT-01"
    )
    @GetMapping("/{sessionId}/summary")
    public ResponseEntity<ApiResponse<?>> summarizeSession(HttpServletRequest httpServletRequest,
                                                           @PathVariable("sessionId") Long sessionId) {
        User user = authService.getUserFromRequest(httpServletRequest);
        SessionSummaryResponse response = sessionService.summarizeSession(user, sessionId);
        return (response != null)?
                ResponseEntity.ok(ApiResponse.success(response)):
                ResponseEntity.internalServerError().body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "세션 결과 요약에 실패했습니다."));
    }
}
