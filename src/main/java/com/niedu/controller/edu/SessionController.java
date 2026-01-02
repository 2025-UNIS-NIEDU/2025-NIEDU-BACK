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
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
@SecurityRequirement(name = "accessToken")
public class SessionController {
    private final SessionService sessionService;
    private final AuthService authService;

    @Operation(
            summary = "특정 코스 내 세션 리스트 조회",
            description = "FUNCTION ID: EDU-DETAIL-05"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "OK",
                    content = @Content(schema = @Schema(
                            implementation = com.niedu.global.response.ApiResponse.class,
                            description = "data: List<SessionListResponse>"
                    ))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Bad Request",
                    content = @Content(schema = @Schema(implementation = com.niedu.global.response.ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = com.niedu.global.response.ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Forbidden",
                    content = @Content(schema = @Schema(implementation = com.niedu.global.response.ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = com.niedu.global.response.ApiResponse.class))
            )
    })
    @GetMapping("")
    public ResponseEntity<ApiResponse<?>> getSessions(
            @Parameter(description = "코스 ID", required = true, example = "1")
            @PathVariable("courseId") Long courseId) {
        ArrayList<SessionListResponse> responses = sessionService.getSessions(courseId);
        return (responses != null)?
                ResponseEntity.ok(ApiResponse.success(responses)):
                ResponseEntity.internalServerError().body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "세션 목록 조회에 실패했습니다."));
    }

    @Operation(
            summary = "특정 코스 내 특정 세션 시작 (step 메타데이터/진입점 step 정보)",
            description = "FUNCTION ID: EDU-QUIZ-STEP-03"
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "학습 난이도 요청",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"level\":\"N\"}")
            )
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "OK",
                    content = @Content(schema = @Schema(
                            implementation = com.niedu.global.response.ApiResponse.class,
                            description = "data: SessionStartResponse"
                    ))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Bad Request",
                    content = @Content(schema = @Schema(implementation = com.niedu.global.response.ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = com.niedu.global.response.ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Forbidden",
                    content = @Content(schema = @Schema(implementation = com.niedu.global.response.ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = com.niedu.global.response.ApiResponse.class))
            )
    })
    @PostMapping("/{sessionId}/start")
    public ResponseEntity<ApiResponse<?>> startSession(HttpServletRequest httpServletRequest,
                                                       @Parameter(description = "코스 ID", required = true, example = "1")
                                                       @PathVariable("courseId") Long courseId,
                                                       @Parameter(description = "세션 ID", required = true, example = "1")
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
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "OK",
                    content = @Content(schema = @Schema(
                            implementation = com.niedu.global.response.ApiResponse.class,
                            description = "data: null"
                    ))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Bad Request",
                    content = @Content(schema = @Schema(implementation = com.niedu.global.response.ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = com.niedu.global.response.ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Forbidden",
                    content = @Content(schema = @Schema(implementation = com.niedu.global.response.ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = com.niedu.global.response.ApiResponse.class))
            )
    })
    @PostMapping("/{sessionId}/quit")
    public ResponseEntity<ApiResponse<?>> quitSession(HttpServletRequest httpServletRequest,
                                                       @Parameter(description = "코스 ID", required = true, example = "1")
                                                       @PathVariable("courseId") Long courseId,
                                                       @Parameter(description = "세션 ID", required = true, example = "1")
                                                       @PathVariable("sessionId") Long sessionId) {
        User user = authService.getUserFromRequest(httpServletRequest);
        sessionService.quitSession(user, sessionId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(
            summary = "학습 세션 마무리",
            description = "FUNCTION ID: EDU-QUIZ-RESULT-01"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "OK",
                    content = @Content(schema = @Schema(
                            implementation = com.niedu.global.response.ApiResponse.class,
                            description = "data: SessionSummaryResponse"
                    ))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Bad Request",
                    content = @Content(schema = @Schema(implementation = com.niedu.global.response.ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = com.niedu.global.response.ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Forbidden",
                    content = @Content(schema = @Schema(implementation = com.niedu.global.response.ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = com.niedu.global.response.ApiResponse.class))
            )
    })
    @GetMapping("/{sessionId}/summary")
    public ResponseEntity<ApiResponse<?>> summarizeSession(HttpServletRequest httpServletRequest,
                                                           @Parameter(description = "코스 ID", required = true, example = "1")
                                                           @PathVariable("courseId") Long courseId,
                                                           @Parameter(description = "세션 ID", required = true, example = "1")
                                                           @PathVariable("sessionId") Long sessionId) {
        User user = authService.getUserFromRequest(httpServletRequest);
        SessionSummaryResponse response = sessionService.summarizeSession(user, sessionId);
        return (response != null)?
                ResponseEntity.ok(ApiResponse.success(response)):
                ResponseEntity.internalServerError().body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "세션 결과 요약에 실패했습니다."));
    }
}
