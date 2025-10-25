package com.niedu.controller.home;

import com.niedu.entity.user.User;
import com.niedu.global.response.ApiResponse;
import com.niedu.dto.home.HomeCourseRecord;
import com.niedu.dto.home.HomeNewsRecord;
import com.niedu.service.auth.AuthService;
import com.niedu.service.home.HomeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "2. 홈 (Home)", description = "홈 화면 관련 API")
@RestController
@RequestMapping("/api/home")
@RequiredArgsConstructor
@Validated
public class HomeController {

    private final HomeService homeService;
    private final AuthService authService;

    @Operation(summary = "오늘자 뉴스 조회", description = "HOM-HOME-02 명세서")
    @GetMapping("/news")
    public ResponseEntity<ApiResponse<?>> getHomeNews(
                                                       HttpServletRequest httpServletRequest
    ) {
        User user = authService.getUserFromRequest(httpServletRequest);

        HomeNewsRecord data = homeService.getRandomNews(user);

        return (data != null) ?
                ResponseEntity.ok(ApiResponse.success(data)) :
                ResponseEntity.internalServerError().body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "오늘자 뉴스 조회에 실패했습니다."));
    }

    @Operation(summary = "홈 내 코스 조회", description = "HOM-HOME-03/04/RECENT-01/SAVED-01 명세서")
    @GetMapping("/courses")
    public ResponseEntity<ApiResponse<?>> getHomeCourses(
                                                          HttpServletRequest httpServletRequest,
                                                          @Parameter(description = "코스 타입: `recent` 또는 `saved`", required = true)
                                                          @RequestParam @Pattern(regexp = "recent|saved") String type,
                                                          @Parameter(description = "뷰 타입: `preview` 또는 `all`", required = true)
                                                          @RequestParam @Pattern(regexp = "preview|all") String view
    ) {
        User user = authService.getUserFromRequest(httpServletRequest);

        HomeCourseRecord.CourseListResponse data = homeService.getCourses(user, type, view);

        return (data != null) ?
                ResponseEntity.ok(ApiResponse.success(data)) :
                ResponseEntity.internalServerError().body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "홈 코스 조회에 실패했습니다."));
    }
}