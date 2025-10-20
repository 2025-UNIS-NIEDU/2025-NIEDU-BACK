package com.niedu.security.oauth;

import com.niedu.entity.user.RefreshToken;
import com.niedu.entity.user.User;
import com.niedu.repository.user.RefreshTokenRepository;
import com.niedu.repository.user.UserRepository;
import com.niedu.security.jwt.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuthLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Value("${jwt.access-token.expiration-time}")
    private long ACCESS_TOKEN_EXPIRATION_TIME;

    @Value("${jwt.refresh-token.expiration-time}")
    private long REFRESH_TOKEN_EXPIRATION_TIME;

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException {

        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        String provider = token.getAuthorizedClientRegistrationId();

        OAuth2UserInfo oAuth2UserInfo;
        if ("kakao".equals(provider)) {
            oAuth2UserInfo = new KakaoUserInfo(token.getPrincipal().getAttributes());
        } else {
            throw new IllegalArgumentException("지원하지 않는 OAuth provider: " + provider);
        }

        String kakaoId = oAuth2UserInfo.getProviderId();
        String nickname = oAuth2UserInfo.getName();
        String profileImageUrl = oAuth2UserInfo.getProfileImageUrl();

        User user = userRepository.findByKakaoId(kakaoId);
        if (user == null) {
            user = User.builder()
                    .kakaoId(kakaoId)
                    .nickname(nickname)
                    .profileImageUrl(profileImageUrl)
                    .build();
            userRepository.save(user);
            log.info("신규 사용자 저장 완료: {}", nickname);
        } else {
            refreshTokenRepository.deleteByUserId(user.getId());
            log.info("기존 사용자 로그인: {}", nickname);
        }

        // 토큰 생성
        String accessToken = jwtUtil.generateAccessToken(user.getId());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId());

        refreshTokenRepository.findByUserId(user.getId())
                .ifPresent(refreshTokenRepository::delete);

        refreshTokenRepository.save(
                RefreshToken.builder()
                        .userId(user.getId())
                        .token(refreshToken)
                        .build()
        );

        // 요청 서버명 기준으로 로컬/운영 판별
        String serverName = request.getServerName();
        boolean isLocal = serverName.equalsIgnoreCase("localhost") || serverName.equals("127.0.0.1");

        // 프론트엔드 Redirect URL 설정
        String redirectUrl;
        if (isLocal) {
            // 👉 로컬 환경: 쿼리 파라미터로 토큰 전달
            redirectUrl = "http://localhost:5173/login/success"
                    + "?accessToken=" + accessToken
                    + "&refreshToken=" + refreshToken;
        } else {
            // 👉 운영 환경: 프론트 배포 주소 (실제 배포 주소로 교체)
            redirectUrl = "https://niedu-service.com/login/success";
        }

        // 운영 환경에서만 쿠키 전달
        if (!isLocal) {
            String domain = ".niedu-service.com";  // 공통 루트 도메인
            boolean secureFlag = true;

            Cookie accessCookie = new Cookie("accessToken", accessToken);
            accessCookie.setHttpOnly(true);
            accessCookie.setSecure(secureFlag);
            accessCookie.setPath("/");
            accessCookie.setDomain(domain);
            accessCookie.setMaxAge((int) (ACCESS_TOKEN_EXPIRATION_TIME / 1000));

            Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
            refreshCookie.setHttpOnly(true);
            refreshCookie.setSecure(secureFlag);
            refreshCookie.setPath("/");
            refreshCookie.setDomain(domain);
            refreshCookie.setMaxAge((int) (REFRESH_TOKEN_EXPIRATION_TIME / 1000));

            response.addCookie(accessCookie);
            response.addCookie(refreshCookie);

            // SameSite=None 직접 헤더로 추가 (사파리 대응)
            response.addHeader("Set-Cookie",
                    String.format("accessToken=%s; Max-Age=%d; Path=/; Domain=%s; Secure; HttpOnly; SameSite=None",
                            accessToken, ACCESS_TOKEN_EXPIRATION_TIME / 1000, domain));
            response.addHeader("Set-Cookie",
                    String.format("refreshToken=%s; Max-Age=%d; Path=/; Domain=%s; Secure; HttpOnly; SameSite=None",
                            refreshToken, REFRESH_TOKEN_EXPIRATION_TIME / 1000, domain));
        }

        // 로그 출력
        log.info("""
                ✅ OAuth 로그인 성공
                - kakaoId: {}
                - serverName: {}
                - redirect: {}
                - mode: {}
                """, kakaoId, serverName, redirectUrl, isLocal ? "LOCAL(파라미터 전달)" : "PROD(쿠키 전달, SameSite=None)");

        // 리다이렉트
        if (!response.isCommitted()) {
            getRedirectStrategy().sendRedirect(request, response, redirectUrl);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}