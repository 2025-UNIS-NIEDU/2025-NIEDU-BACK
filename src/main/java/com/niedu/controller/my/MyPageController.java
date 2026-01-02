package com.niedu.controller.my;

import com.niedu.dto.my.MyCalendarResponse;
import com.niedu.dto.course.content.TermContent;
import com.niedu.dto.my.MyTermListResponse;
import com.niedu.dto.my.ReviewNoteItemResponse;
import com.niedu.entity.user.User;
import com.niedu.global.response.ApiResponse;
import com.niedu.service.auth.AuthService;
import com.niedu.service.my.MyPageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
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
    private final AuthService authService;

    @Operation(
            summary = "캘린더 - 날짜 내비게이터",
            description = "FUNCTION ID: SET-ALL-02"
    )
    @GetMapping("/date-navigator")
    public ResponseEntity<ApiResponse<MyCalendarResponse>> getDateNavigator(
            @Parameter(hidden = true) HttpServletRequest request
    ) {
        User user = authService.getUserFromRequest(request);
        MyCalendarResponse data = myPageService.getDateNavigator(user);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @Operation(
            summary = "캘린더 - 캘린더",
            description = "FUNCTION ID: SET-ALL-03"
    )
    @GetMapping("/calendar")
    public ResponseEntity<ApiResponse<MyCalendarResponse>> getCalendar(
            @Parameter(hidden = true) HttpServletRequest request,
            @Parameter(description = "조회할 연도", required = true) @RequestParam Integer year,
            @Parameter(description = "조회할 월", required = true) @RequestParam Integer month
    ) {
        User user = authService.getUserFromRequest(request);
        MyCalendarResponse data = myPageService.getCalendar(user, year, month);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @Operation(
            summary = "복습 노트 내 날짜별 문제 조회. 초기 진입 시에는 금일 기준으로 요청, 이후에는 선택일 기준으로 요청",
            description = "FUNCTION ID: SET-REVIEW-01, SET-REVIEW-02, SET-REVIEW-03"
    )
    @GetMapping("/review-notes")
    public ResponseEntity<ApiResponse<List<ReviewNoteItemResponse>>> getReviewNotes(
            @Parameter(hidden = true) HttpServletRequest request,
            @Parameter(description = "조회할 날짜 (YYYY-MM-DD)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        User user = authService.getUserFromRequest(request);
        List<ReviewNoteItemResponse> data = myPageService.getReviewNotes(user, date);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @Operation(
            summary = "용어 사전 내 전체 용어 조회 (가나다순: alphabetical, 최근저장순: recent)",
            description = "FUNCTION ID: SET-DICTIONARY-03"
    )
    @GetMapping("/terms")
    public ResponseEntity<ApiResponse<MyTermListResponse>> getAllMyTerms(
            @Parameter(hidden = true) HttpServletRequest request,
            @Parameter(description = "정렬 기준 (alphabetical, recent)", required = true)
            @RequestParam @Pattern(regexp = "alphabetical|recent", message = "sort 파라METER는 'alphabetical' 또는 'recent'만 가능합니다.") String sort
    ) {
        User user = authService.getUserFromRequest(request);
        MyTermListResponse data = myPageService.getAllMyTerms(user, sort);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @Operation(
            summary = "용어 사전 내 특정 용어 조회",
            description = "FUNCTION ID: SET-DICTIONARY-04"
    )
    @GetMapping("/terms/{termId}")
    public ResponseEntity<ApiResponse<TermContent>> getMyTermById(
                                                                   @Parameter(hidden = true) HttpServletRequest request, // --- [수정] ---
                                                                   @Parameter(description = "조회할 용어 ID", required = true) @PathVariable Long termId
    ) {
        User user = authService.getUserFromRequest(request);
        TermContent data = myPageService.getMyTermById(user, termId);
        return ResponseEntity.ok(ApiResponse.success(data));
    }
}
