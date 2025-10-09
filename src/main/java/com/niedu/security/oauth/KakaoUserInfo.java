package com.niedu.security.oauth;

import java.util.Map;

/**
 * Kakao OAuth2 사용자 정보 매핑 클래스
 * 공식 문서: https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#req-user-info
 */
public class KakaoUserInfo implements OAuth2UserInfo {

    private final Map<String, Object> attributes;

    public KakaoUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    /**
     * 카카오 내부 고유 사용자 ID
     * JSON 경로: id
     */
    @Override
    public String getProviderId() {
        return String.valueOf(attributes.get("id"));
    }

    /**
     * OAuth 제공자명
     * 고정값 "kakao"
     */
    @Override
    public String getProvider() {
        return "kakao";
    }

    /**
     * 사용자 닉네임
     * JSON 경로: kakao_account.profile.nickname
     */
    @Override
    public String getName() {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        if (kakaoAccount == null) return null;

        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
        if (profile == null) return null;

        return (String) profile.get("nickname");
    }

    /**
     * 사용자 프로필 이미지 URL
     * JSON 경로: kakao_account.profile.profile_image_url
     */
    @Override
    public String getProfileImageUrl() {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        if (kakaoAccount == null) return null;

        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
        if (profile == null) return null;

        return (String) profile.get("profile_image_url");
    }
}
