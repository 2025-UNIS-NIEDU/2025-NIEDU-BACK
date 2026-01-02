package com.niedu.controller.favorite;

import com.niedu.dto.favorite.FavoriteRequest;
import com.niedu.dto.favorite.IsFavoriteResponse;
import com.niedu.entity.user.User;
import com.niedu.global.enums.FavoriteType;
import com.niedu.global.response.ApiResponse;
import com.niedu.service.auth.AuthService;
import com.niedu.service.favorite.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
@Tag(name = "즐겨찾기", description = "코스/용어 즐겨찾기 관련 API")
public class FavoriteController {
    private final AuthService authService;
    private final FavoriteService favoriteService;

    @Operation(
            summary = "즐겨찾기 추가",
            description = "FUNCTION ID: SRH-AFTER-03, EDU-DETAIL-02, EDU-QUIZ-02-N-01, EDU-QUIZ-02-N-02, SET-DICTIONARY-04"
    )
    @PostMapping
    public ResponseEntity<ApiResponse<?>> addFavorite(HttpServletRequest httpServletRequest,
                                                      @RequestBody FavoriteRequest request) {
        User user = authService.getUserFromRequest(httpServletRequest);
        favoriteService.addFavorite(user, request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(
            summary = "즐겨찾기 삭제",
            description = "FUNCTION ID: SRH-AFTER-03, EDU-DETAIL-02, EDU-QUIZ-02-N-01, EDU-QUIZ-02-N-02, SET-DICTIONARY-04"
    )
    @DeleteMapping
    public ResponseEntity<ApiResponse<?>> deleteFavorite(HttpServletRequest httpServletRequest,
                                                         @RequestBody FavoriteRequest request) {
        User user = authService.getUserFromRequest(httpServletRequest);
        favoriteService.deleteFavorite(user, request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(
            summary = "즐겨찾기 여부 판단 (type: COURSE/TERM, targetId: 즐겨찾기 대상 식별자)",
            description = "FUNCTION ID: SRH-AFTER-03, EDU-DETAIL-02, EDU-QUIZ-02-N-01, EDU-QUIZ-02-N-02, SET-DICTIONARY-04"
    )
    @GetMapping("/{type}/{targetId}")
    public ResponseEntity<ApiResponse<?>> checkIsFavorite(HttpServletRequest httpServletRequest,
                                                          @PathVariable("type") FavoriteType type,
                                                          @PathVariable("targetId") Long targetId) {
        User user = authService.getUserFromRequest(httpServletRequest);
        IsFavoriteResponse response = favoriteService.checkIsFavorite(user, type, targetId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
