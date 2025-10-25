package com.niedu.controller.home;

import com.niedu.global.response.ApiResponse;
import com.niedu.dto.home.HomeCourseRecord;
import com.niedu.dto.home.HomeNewsRecord;
import com.niedu.service.home.HomeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
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

    @Operation(summary = "오늘자 뉴스 조회", description = "HOM-HOME-02 명세서")
    @GetMapping("/news")
    public ResponseEntity<ApiResponse<HomeNewsRecord>> getHomeNews(
            @CookieValue(name = "accessToken") String accessToken // Assuming token is needed
    ) {
        HomeNewsRecord data = homeService.getRandomNews();
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @Operation(summary = "홈 내 코스 조회", description = "HOM-HOME-03/04/RECENT-01/SAVED-01 명세서")
    @GetMapping("/courses")
    public ResponseEntity<ApiResponse<HomeCourseRecord.CourseListResponse>> getHomeCourses(
            @CookieValue(name = "accessToken") String accessToken, // Assuming token is needed
            @Parameter(description = "코스 타입: `recent` 또는 `saved`", required = true)
            @RequestParam @Pattern(regexp = "recent|saved") String type,
            @Parameter(description = "뷰 타입: `preview` 또는 `all`", required = true)
            @RequestParam @Pattern(regexp = "preview|all") String view
    ) {
        Long userId = 1L;
        HomeCourseRecord.CourseListResponse data = homeService.getCourses(userId, type, view);
        return ResponseEntity.ok(ApiResponse.success(data));
    }
}