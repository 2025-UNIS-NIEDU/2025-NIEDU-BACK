package com.niedu.security.oauth;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.niedu.entity.user.RefreshToken;
import com.niedu.entity.user.User;
import com.niedu.repository.user.RefreshTokenRepository;
import com.niedu.repository.user.UserRepository;
import com.niedu.security.TokenCookieSupport;
import com.niedu.security.jwt.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuthLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Value("${jwt.access-token.expiration-time}")
    private long ACCESS_TOKEN_EXPIRATION_TIME;

    @Value("${jwt.refresh-token.expiration-time}")
    private long REFRESH_TOKEN_EXPIRATION_TIME;

    @Value("${app.frontend.redirect-url.local:${app.frontend.redirect-url}}")
    private String frontendRedirectUrlLocal;

    @Value("${app.frontend.redirect-url.vercel:${app.frontend.redirect-url}}")
    private String frontendRedirectUrlVercel;

    @Value("${app.frontend.redirect-url.prod:${app.frontend.redirect-url}}")
    private String frontendRedirectUrlProd;

    @Value("${app.cookie.domain:}")
    private String cookieDomain;

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final HttpCookieOAuth2AuthorizationRequestRepository authorizationRequestRepository;

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

        // 1. 요청 서버명 기준으로 로컬/운영 판별
        boolean isLocalRequest = TokenCookieSupport.isLocalRequest(request);

        // 2. 클라이언트 파라미터 기준으로 분기 (local|vercel|prod)
        String clientParam = authorizationRequestRepository.resolveClientParam(request);
        FrontendClient frontendClient = resolveFrontendClient(clientParam, isLocalRequest);

        // 3. 프론트엔드 Redirect URL 결정
        String redirectUrl = resolveRedirectUrl(frontendClient);

        if (frontendClient == FrontendClient.LOCAL) {
            // 4. 로컬 환경: 토큰을 쿼리 파라미터로 전달
            redirectUrl = buildRedirectUrlWithTokens(redirectUrl, accessToken, refreshToken);
        } else {
            // 5. 운영/버셀 환경: 쿠키로 전달 (SameSite=None, Secure)
            String domain = normalizeCookieDomain(cookieDomain);
            boolean secureFlag = true;
            String sameSite = "None";

            TokenCookieSupport.addTokenCookie(
                    response,
                    "accessToken",
                    accessToken,
                    (int) (ACCESS_TOKEN_EXPIRATION_TIME / 1000),
                    domain,
                    secureFlag,
                    sameSite
            );
            TokenCookieSupport.addTokenCookie(
                    response,
                    "refreshToken",
                    refreshToken,
                    (int) (REFRESH_TOKEN_EXPIRATION_TIME / 1000),
                    domain,
                    secureFlag,
                    sameSite
            );
        }

        // 로그 출력
        log.info("""
                ✅ OAuth 로그인 성공
                - kakaoId: {}
                - redirect: {}
                - mode: {}
                - client: {}
                """, kakaoId, redirectUrl, frontendClient, clientParam);

        // 리다이렉트
        if (!response.isCommitted()) {
            getRedirectStrategy().sendRedirect(request, response, redirectUrl);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private String buildRedirectUrlWithTokens(String baseUrl, String accessToken, String refreshToken) {
        String encodedAccessToken = URLEncoder.encode(accessToken, StandardCharsets.UTF_8);
        String encodedRefreshToken = URLEncoder.encode(refreshToken, StandardCharsets.UTF_8);
        return baseUrl + "?accessToken=" + encodedAccessToken + "&refreshToken=" + encodedRefreshToken;
    }

    private FrontendClient resolveFrontendClient(String clientParam, boolean isLocalRequest) {
        if (clientParam == null || clientParam.isBlank()) {
            return isLocalRequest ? FrontendClient.LOCAL : FrontendClient.PROD;
        }
        String normalized = clientParam.trim().toLowerCase();
        return switch (normalized) {
            case "local" -> FrontendClient.LOCAL;
            case "vercel" -> FrontendClient.VERCEL;
            case "prod" -> FrontendClient.PROD;
            default -> isLocalRequest ? FrontendClient.LOCAL : FrontendClient.PROD;
        };
    }

    private String resolveRedirectUrl(FrontendClient frontendClient) {
        return switch (frontendClient) {
            case LOCAL -> frontendRedirectUrlLocal;
            case VERCEL -> frontendRedirectUrlVercel;
            case PROD -> frontendRedirectUrlProd;
        };
    }

    private String normalizeCookieDomain(String domain) {
        if (domain == null || domain.isBlank()) {
            return null;
        }
        String trimmed = domain.trim();
        return trimmed.startsWith(".") ? trimmed.substring(1) : trimmed;
    }

    private enum FrontendClient {
        LOCAL,
        VERCEL,
        PROD
    }
}
