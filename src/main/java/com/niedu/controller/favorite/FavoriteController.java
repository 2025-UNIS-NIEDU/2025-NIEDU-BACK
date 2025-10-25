package com.niedu.controller.favorite;

import com.niedu.dto.favorite.FavoriteRequest;
import com.niedu.dto.favorite.IsFavoriteResponse;
import com.niedu.entity.user.User;
import com.niedu.global.enums.FavoriteType;
import com.niedu.global.response.ApiResponse;
import com.niedu.service.auth.AuthService;
import com.niedu.service.favorite.FavoriteService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {
    private final AuthService authService;
    private final FavoriteService favoriteService;

    @PostMapping
    public ResponseEntity<ApiResponse<?>> addFavorite(HttpServletRequest httpServletRequest,
                                                      @RequestBody FavoriteRequest request) {
        User user = authService.getUserFromRequest(httpServletRequest);
        favoriteService.addFavorite(user, request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<?>> deleteFavorite(HttpServletRequest httpServletRequest,
                                                         @RequestBody FavoriteRequest request) {
        User user = authService.getUserFromRequest(httpServletRequest);
        favoriteService.deleteFavorite(user, request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/{type}/{targetId}")
    public ResponseEntity<ApiResponse<?>> checkIsFavorite(HttpServletRequest httpServletRequest,
                                                          @PathVariable("type") FavoriteType type,
                                                          @PathVariable("targetId") Long targetId) {
        User user = authService.getUserFromRequest(httpServletRequest);
        IsFavoriteResponse response = favoriteService.checkIsFavorite(user, type, targetId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
