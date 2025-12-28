package com.niedu.security.oauth;

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
        boolean isLocal = TokenCookieSupport.isLocalRequest(request);

        // 프론트엔드 Redirect URL 설정
        String redirectUrl;
        if (isLocal) {
            // 👉 로컬 환경
            redirectUrl = "http://localhost:5173/login/success";
        } else {
            // 👉 운영 환경: 프론트 배포 주소 (실제 배포 주소로 교체)
            redirectUrl = "https://niedu-service.com/login/success";
        }

        String domain = isLocal ? null : ".niedu-service.com";
        boolean secureFlag = !isLocal;
        String sameSite = isLocal ? "Lax" : "None";

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

        // 로그 출력
        log.info("""
                ✅ OAuth 로그인 성공
                - kakaoId: {}
                - redirect: {}
                - mode: {}
                """, kakaoId, redirectUrl, isLocal ? "LOCAL(쿠키 전달)" : "PROD(쿠키 전달, SameSite=None)");

        // 리다이렉트
        if (!response.isCommitted()) {
            getRedirectStrategy().sendRedirect(request, response, redirectUrl);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
