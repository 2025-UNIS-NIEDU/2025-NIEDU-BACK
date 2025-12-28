package com.niedu.controller.search;

import com.niedu.dto.search.CourseSearchResponse;
import com.niedu.dto.search.SearchHistoryResponse;
import com.niedu.entity.user.User;
import com.niedu.global.response.ApiResponse;
import com.niedu.service.auth.AuthService;
import com.niedu.service.search.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "7. 검색 (Search)", description = "검색 관련 API")
@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
@Validated
public class SearchController {

    private final AuthService authService;
    private final SearchService searchService;

    @Operation(summary = "이전 검색어 목록 조회", description = "SRH-BEFORE-02, SRH-BEFORE-03 명세서")
    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<SearchHistoryResponse>>> getSearchHistory(HttpServletRequest httpServletRequest) {
        User user = authService.getUserFromRequest(httpServletRequest);

        List<SearchHistoryResponse> responses = searchService.getSearchHistory(user);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @Operation(summary = "이전 검색어 삭제", description = "SRH-BEFORE-?? 명세서")
    @DeleteMapping("/history/{logId}")
    public ResponseEntity<ApiResponse<?>> deleteSearchHistory(
            @Parameter(description = "삭제할 검색 기록 ID", required = true) @PathVariable Long logId
    ) {

        searchService.deleteSearchHistory(logId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "추천 검색어 목록 조회", description = "SRH-BEFORE-01 명세서")
    @GetMapping("/suggestions")
    public ResponseEntity<ApiResponse<List<String>>> getSearchSuggestions() {
        List<String> response = searchService.getSearchSuggestions();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "코스 검색 결과 조회", description = "SRH-AFTER-02, SRH-AFTER-03 명세서")
    @GetMapping("/courses")
    public ResponseEntity<ApiResponse<List<CourseSearchResponse>>> searchCourses(
            HttpServletRequest httpServletRequest,
            @Parameter(description = "검색할 키워드", required = true) @RequestParam @NotBlank String keyword,
            @Parameter(description = "정렬 기준: `recent` (최신순) 또는 `popular` (인기순)", required = true)
            @RequestParam @Pattern(regexp = "recent|popular", message = "sort 파라미터는 'recent' 또는 'popular'만 가능합니다.") String sort,
            @Parameter(description = "페이지 번호 (0부터 시작)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 당 항목 수")
            @RequestParam(defaultValue = "10") int size
    ) {
        User user = authService.getUserFromRequest(httpServletRequest);
        List<CourseSearchResponse> responses = searchService.searchCourses(user, keyword, sort, page, size);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }
}
