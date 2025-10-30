package com.niedu.controller.my;

import com.niedu.dto.my.MyCalendarResponse;
import com.niedu.dto.my.MyTermDetailResponse;
import com.niedu.dto.my.MyTermListResponse;
import com.niedu.dto.my.ReviewNoteItemResponse;
import com.niedu.global.response.ApiResponse;
import com.niedu.security.jwt.JwtUtil;
import com.niedu.service.my.MyPageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "8. 마이페이지 (My Page)", description = "캘린더, 복습노트, 용어사전 관련 API")
@RestController
@RequestMapping("/api/my")
@RequiredArgsConstructor
@Validated
public class MyPageController {

    private final MyPageService myPageService;
    private final JwtUtil jwtUtil;

    @Operation(summary = "캘린더 - 날짜 내비게이터 (SET-ALL-02)")
    @GetMapping("/date-navigator")
    public ResponseEntity<ApiResponse<MyCalendarResponse>> getDateNavigator(
            @Parameter(hidden = true) @CookieValue(name = "accessToken") String accessToken
    ) {
        Long userId = Long.parseLong(jwtUtil.extractUsername(accessToken));
        MyCalendarResponse data = myPageService.getDateNavigator(userId);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @Operation(summary = "캘린더 - 캘린더 (SET-ALL-03)")
    @GetMapping("/calendar")
    public ResponseEntity<ApiResponse<MyCalendarResponse>> getCalendar(
            @Parameter(hidden = true) @CookieValue(name = "accessToken") String accessToken,
            @Parameter(description = "조회할 연도", required = true) @RequestParam Integer year,
            @Parameter(description = "조회할 월", required = true) @RequestParam Integer month
    ) {
        Long userId = Long.parseLong(jwtUtil.extractUsername(accessToken));
        MyCalendarResponse data = myPageService.getCalendar(userId, year, month);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @Operation(summary = "복습 노트 내 날짜별 문제 조회 (SET-REVIEW-01~03)")
    @GetMapping("/review-notes")
    public ResponseEntity<ApiResponse<List<ReviewNoteItemResponse>>> getReviewNotes(
            @Parameter(hidden = true) @CookieValue(name = "accessToken") String accessToken,
            @Parameter(description = "조회할 날짜 (YYYY-MM-DD)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        Long userId = Long.parseLong(jwtUtil.extractUsername(accessToken));
        List<ReviewNoteItemResponse> data = myPageService.getReviewNotes(userId, date);
        return ResponseEntity.ok(ApiResponse.success(data));
    }


    @Operation(summary = "용어 사전 내 전체 용어 조회 (SET-DICTIONARY-03)")
    @GetMapping("/terms")
    public ResponseEntity<ApiResponse<MyTermListResponse>> getAllMyTerms(
            @Parameter(hidden = true) @CookieValue(name = "accessToken") String accessToken,
            @Parameter(description = "정렬 기준 (alphabetical, recent)", required = true)
            @RequestParam @Pattern(regexp = "alphabetical|recent", message = "sort 파라미터는 'alphabetical' 또는 'recent'만 가능합니다.") String sort
    ) {
        Long userId = Long.parseLong(jwtUtil.extractUsername(accessToken));
        MyTermListResponse data = myPageService.getAllMyTerms(userId, sort);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @Operation(summary = "용어 사전 내 특정 용어 조회 (SET-DICTIONARY-04)", description = "저장한 특정 용어 상세 조회")
    @GetMapping("/terms/{termId}")
    public ResponseEntity<ApiResponse<MyTermDetailResponse>> getMyTermById(
            @Parameter(hidden = true) @CookieValue(name = "accessToken") String accessToken,
            @Parameter(description = "조회할 용어 ID", required = true) @PathVariable Long termId
    ) {
        Long userId = Long.parseLong(jwtUtil.extractUsername(accessToken));
        MyTermDetailResponse data = myPageService.getMyTermById(userId, termId);
        return ResponseEntity.ok(ApiResponse.success(data));
    }
}