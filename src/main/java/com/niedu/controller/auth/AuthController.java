package com.niedu.controller.auth;

import com.niedu.entity.user.RefreshToken;
import com.niedu.repository.user.RefreshTokenRepository;
import com.niedu.security.CookieUtils;
import com.niedu.security.TokenCookieSupport;
import com.niedu.security.jwt.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "회원관리", description = "회원/인증 관련 API")
public class AuthController {

    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.access-token.expiration-time}")
    private long ACCESS_TOKEN_EXPIRATION_TIME;

    @Value("${app.cookie.domain:}")
    private String cookieDomain;

    @Operation(summary = "액세스 토큰 재발급", description = "FUNCTION ID: 없음")
    @PostMapping("/reissue-access-token")
    public void reissueAccessToken(HttpServletRequest request, HttpServletResponse response) {
        try {
            // 쿠키에서 refreshToken 찾기
            String refreshToken = CookieUtils.getCookieValue(request, "refreshToken")
                    .orElseThrow(() -> new RuntimeException("RefreshToken 쿠키 없음"));
            refreshToken = URLDecoder.decode(refreshToken, StandardCharsets.UTF_8);
            if (refreshToken.startsWith("Bearer ")) {
                refreshToken = refreshToken.substring(7);
            }
            log.debug("Reissue access token: refreshToken cookie length={}", refreshToken.length());

            // Refresh Token 검증
            String userId = jwtUtil.extractUsername(refreshToken);
            log.debug("Reissue access token: extracted userId={}", userId);
            if (jwtUtil.extractExpiration(refreshToken).before(new Date())) {
                throw new RuntimeException("RefreshToken 만료됨");
            }
            log.debug("Reissue access token: refreshToken not expired");

            // DB 내 저장된 Refresh Token과 일치 여부 확인
            RefreshToken saved = refreshTokenRepository.findByUserId(Long.parseLong(userId)).orElse(null);
            if (saved == null || !saved.getToken().equals(refreshToken)) {
                throw new RuntimeException("RefreshToken 불일치");
            }
            log.debug("Reissue access token: refreshToken matched DB");

            // Access Token 발급
            String newAccessToken = jwtUtil.generateAccessToken(Long.parseLong(userId));

            // 환경 구분
            boolean isLocal = TokenCookieSupport.isLocalRequest(request);
            String domain = isLocal ? null : normalizeCookieDomain(cookieDomain);
            boolean secureFlag = !isLocal;
            String sameSite = isLocal ? "Lax" : "None";

            TokenCookieSupport.addTokenCookie(
                    response,
                    "accessToken",
                    newAccessToken,
                    (int) (ACCESS_TOKEN_EXPIRATION_TIME / 1000),
                    domain,
                    secureFlag,
                    sameSite
            );

            response.setStatus(HttpServletResponse.SC_OK);
            log.info("AccessToken 쿠키 재발급 완료 (userId: {}, mode: {})", userId, isLocal ? "LOCAL" : "PROD");

        } catch (Exception e) {
            log.error("Access Token 재발급 실패: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    private String normalizeCookieDomain(String domain) {
        if (domain == null || domain.isBlank()) {
            return null;
        }
        String trimmed = domain.trim();
        return trimmed.startsWith(".") ? trimmed.substring(1) : trimmed;
    }
}
