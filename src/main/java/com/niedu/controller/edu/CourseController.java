package com.niedu.controller.edu;

import com.niedu.dto.course.CourseListResponse;
import com.niedu.dto.course.CourseResponse;
import com.niedu.entity.user.User;
import com.niedu.global.response.ApiResponse;
import com.niedu.service.auth.AuthService;
import com.niedu.service.edu.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
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
@RequestMapping("/api/edu/courses")
@RequiredArgsConstructor
@Tag(name = "학습", description = "코스/세션/스텝 학습 관련 API")
@SecurityRequirement(name = "accessToken")
public class CourseController {
    private final CourseService courseService;
    private final AuthService authService;

    @Operation(
            summary = "코스 조회 (type: RECENT/POPULAR/CUSTOM/NEW, view: PREVIEW/ALL, topic: 정치/경제/사회/국제, page: 무한스크롤용)",
            description = "FUNCTION ID: EDU-EDU-02, EDU-EDU-03, EDU-EDU-04, EDU-EDU-05, EDU-RECENT-01, EDU-HOT-01, EDU-PERSONALIZED-01, EDU-NEW-01"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "OK",
                    content = @Content(schema = @Schema(
                            implementation = com.niedu.global.response.ApiResponse.class,
                            description = "data: List<CourseListResponse>"
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
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getCourses(HttpServletRequest httpServletRequest,
                                                     @Parameter(
                                                             description = "코스 타입: RECENT/POPULAR/CUSTOM/NEW",
                                                             required = true,
                                                             example = "RECENT"
                                                     )
                                                     @RequestParam("type") String type,
                                                     @Parameter(
                                                             description = "뷰 타입: PREVIEW/ALL",
                                                             required = true,
                                                             example = "PREVIEW"
                                                     )
                                                     @RequestParam("view") String view,
                                                     @Parameter(
                                                             description = "토픽: 정치/경제/사회/국제 (선택)",
                                                             example = "경제"
                                                     )
                                                     @RequestParam(value = "topic", required = false) String topic,
                                                     @Parameter(
                                                             description = "페이지 번호 (무한스크롤용, 0부터 시작)",
                                                             example = "0"
                                                     )
                                                     @RequestParam(defaultValue = "0", required = false) Integer page) {
        User user = authService.getUserFromRequest(httpServletRequest);
        ArrayList<CourseListResponse> responses = courseService.getCourses(user, type, view, topic, page);
        return (responses != null)?
                ResponseEntity.ok(ApiResponse.success(responses)):
                ResponseEntity.internalServerError().body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "코스 목록 조회에 실패했습니다."));
    }

    @Operation(
            summary = "특정 코스 상세 조회",
            description = "FUNCTION ID: EDU-DETAIL-03"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "OK",
                    content = @Content(schema = @Schema(
                            implementation = com.niedu.global.response.ApiResponse.class,
                            description = "data: CourseResponse"
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
    @GetMapping("/{courseId}")
    public ResponseEntity<ApiResponse<?>> getCourse(HttpServletRequest httpServletRequest,
                                                    @Parameter(
                                                            description = "코스 ID",
                                                            required = true,
                                                            example = "1"
                                                    )
                                                    @PathVariable("courseId") Long courseId) {
        User user = authService.getUserFromRequest(httpServletRequest);
        CourseResponse response = courseService.getCourse(user, courseId);
        return (response != null)?
                ResponseEntity.ok(ApiResponse.success(response)):
                ResponseEntity.internalServerError().body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "코스 조회에 실패했습니다."));
    }
}
