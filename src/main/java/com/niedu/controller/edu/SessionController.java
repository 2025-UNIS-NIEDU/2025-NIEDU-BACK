package com.niedu.controller.edu;

import com.niedu.dto.course.LevelRequest;
import com.niedu.dto.course.SessionListResponse;
import com.niedu.dto.course.SessionStartResponse;
import com.niedu.dto.course.SessionSummaryResponse;
import com.niedu.entity.user.User;
import com.niedu.global.response.ApiResponse;
import com.niedu.service.auth.AuthService;
import com.niedu.service.edu.SessionService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/api/edu/courses/{courseId}/sessions")
@RequiredArgsConstructor
public class SessionController {
    private final SessionService sessionService;
    private final AuthService authService;

    @GetMapping("")
    public ResponseEntity<ApiResponse<?>> getSessions(@PathVariable("courseId") Long courseId) {
        ArrayList<SessionListResponse> responses = sessionService.getSessions(courseId);
        return (responses != null)?
                ResponseEntity.ok(ApiResponse.success(responses)):
                ResponseEntity.internalServerError().body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "세션 목록 조회에 실패했습니다."));
    }

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

    @PostMapping("/{sessionId}/quit")
    public ResponseEntity<ApiResponse<?>> quitSession(HttpServletRequest httpServletRequest,
                                                       @PathVariable("sessionId") Long sessionId) {
        User user = authService.getUserFromRequest(httpServletRequest);
        sessionService.quitSession(user, sessionId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

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
