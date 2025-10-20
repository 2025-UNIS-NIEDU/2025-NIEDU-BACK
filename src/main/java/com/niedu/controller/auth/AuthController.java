package com.niedu.controller.auth;

import com.niedu.entity.user.RefreshToken;
import com.niedu.repository.RefreshTokenRepository;
import com.niedu.security.jwt.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.access-token.expiration-time}")
    private long ACCESS_TOKEN_EXPIRATION_TIME;

    @PostMapping("/reissue-access-token")
    public void reissueAccessToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            // 쿠키에서 refreshToken 찾기
            Cookie[] cookies = request.getCookies();
            if (cookies == null) throw new RuntimeException("쿠키가 존재하지 않습니다.");

            Cookie refreshCookie = Arrays.stream(cookies)
                    .filter(c -> c.getName().equals("refreshToken"))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("RefreshToken 쿠키 없음"));

            String refreshToken = refreshCookie.getValue();

            // Refresh Token 검증
            String userId = jwtUtil.extractUsername(refreshToken);
            if (jwtUtil.extractExpiration(refreshToken).before(new Date())) {
                throw new RuntimeException("RefreshToken 만료됨");
            }

            // DB 내 저장된 Refresh Token과 일치 여부 확인
            RefreshToken saved = refreshTokenRepository.findByUserId(Long.parseLong(userId)).orElse(null);
            if (saved == null || !saved.getToken().equals(refreshToken)) {
                throw new RuntimeException("RefreshToken 불일치");
            }

            // Access Token 발급
            String newAccessToken = jwtUtil.generateAccessToken(Long.parseLong(userId));

            // 환경 구분
            String serverName = request.getServerName();
            boolean isLocal = serverName.equalsIgnoreCase("localhost") || serverName.equals("127.0.0.1");

            if (isLocal) {
                // 로컬: 쿼리 파라미터로 accessToken 전달
                String redirectUrl = "http://localhost:5173/login/success?accessToken=" + newAccessToken;
                log.info("로컬 환경: 쿼리 파라미터로 AccessToken 전달 → {}", redirectUrl);
                response.sendRedirect(redirectUrl);
            } else {
                // 운영: 쿠키로 accessToken 전달
                String domain = ".niedu-service.com";
                Cookie accessCookie = new Cookie("accessToken", newAccessToken);
                accessCookie.setHttpOnly(true);
                accessCookie.setSecure(true);
                accessCookie.setPath("/");
                accessCookie.setDomain(domain);
                accessCookie.setMaxAge((int) (ACCESS_TOKEN_EXPIRATION_TIME / 1000));
                response.addCookie(accessCookie);

                // SameSite=None 명시 (사파리 등 대응)
                response.addHeader("Set-Cookie",
                        String.format("accessToken=%s; Max-Age=%d; Path=/; Domain=%s; Secure; HttpOnly; SameSite=None",
                                newAccessToken, ACCESS_TOKEN_EXPIRATION_TIME / 1000, domain));

                response.setStatus(HttpServletResponse.SC_OK);
                log.info("운영 환경: AccessToken 쿠키로 전달 완료 (userId: {})", userId);
            }

        } catch (Exception e) {
            log.error("Access Token 재발급 실패: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}
