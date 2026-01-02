package com.niedu.controller.home;

import com.niedu.entity.user.User;
import com.niedu.global.response.ApiResponse;
import com.niedu.dto.home.HomeCourseRecord;
import com.niedu.dto.home.HomeNewsRecord;
import com.niedu.service.auth.AuthService;
import com.niedu.service.home.HomeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "2. 홈 (Home)", description = "홈 화면 관련 API")
@SecurityRequirement(name = "accessToken")
@RestController
@RequestMapping("/api/home")
@RequiredArgsConstructor
@Validated
public class HomeController {

    private final HomeService homeService;
    private final AuthService authService;

    @Operation(
            summary = "오전 8시 기준으로 자동 갱신된 최신 뉴스 중 2개 랜덤 응답",
            description = "FUNCTION ID: HOM-HOME-02"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "OK",
                    content = @Content(schema = @Schema(
                            implementation = com.niedu.global.response.ApiResponse.class,
                            description = "data: List<HomeNewsRecord>"
                    ))
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
    @GetMapping("/news")
    public ResponseEntity<ApiResponse<?>> getHomeNews(
                                                       HttpServletRequest httpServletRequest
    ) {
        User user = authService.getUserFromRequest(httpServletRequest);

        List<HomeNewsRecord> responses = homeService.getRandomNews(user);

        return (responses != null) ?
                ResponseEntity.ok(ApiResponse.success(responses)) :
                ResponseEntity.internalServerError().body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "오늘자 뉴스 조회에 실패했습니다."));
    }

    @Operation(
            summary = "홈 내 코스 조회 (type: recent/saved, view: all/preview)",
            description = "FUNCTION ID: HOM-HOME-03, HOM-HOME-04, HOM-RECENT-01, HOM-SAVED-01"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "OK",
                    content = @Content(schema = @Schema(
                            implementation = com.niedu.global.response.ApiResponse.class,
                            description = "data: List<HomeCourseRecord>"
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
    @GetMapping("/courses")
    public ResponseEntity<ApiResponse<?>> getHomeCourses(
                                                          HttpServletRequest httpServletRequest,
                                                          @Parameter(
                                                                  description = "코스 타입: `recent` 또는 `saved`",
                                                                  required = true,
                                                                  example = "recent"
                                                          )
                                                          @RequestParam @Pattern(regexp = "recent|saved") String type,
                                                          @Parameter(
                                                                  description = "뷰 타입: `preview` 또는 `all`",
                                                                  required = true,
                                                                  example = "preview"
                                                          )
                                                          @RequestParam @Pattern(regexp = "preview|all") String view
    ) {
        User user = authService.getUserFromRequest(httpServletRequest);

        List<HomeCourseRecord> responses = homeService.getCourses(user, type, view);

        return (responses != null) ?
                ResponseEntity.ok(ApiResponse.success(responses)) :
                ResponseEntity.internalServerError().body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "홈 코스 조회에 실패했습니다."));
    }
}
