package com.niedu.security.oauth;

public interface OAuth2UserInfo {
    String getProviderId();
    String getProvider();
    String getName();
    String getProfileImageUrl();
}