package com.niedu.security.oauth;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import java.util.Base64;
import java.util.Optional;

@Component
public class HttpCookieOAuth2AuthorizationRequestRepository
        implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    public static final String AUTH_REQUEST_COOKIE_NAME = "oauth2_auth_request";
    public static final String CLIENT_PARAM_COOKIE_NAME = "oauth2_client";
    private static final int COOKIE_EXPIRE_SECONDS = 180;
    private static final String SAME_SITE = "Lax";

    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        return getCookie(request, AUTH_REQUEST_COOKIE_NAME)
                .map(Cookie::getValue)
                .map(this::deserialize)
                .orElse(null);
    }

    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest,
                                         HttpServletRequest request,
                                         HttpServletResponse response) {
        if (authorizationRequest == null) {
            deleteCookie(response, request, AUTH_REQUEST_COOKIE_NAME);
            deleteCookie(response, request, CLIENT_PARAM_COOKIE_NAME);
            return;
        }

        String serialized = serialize(authorizationRequest);
        addCookie(response, request, AUTH_REQUEST_COOKIE_NAME, serialized, COOKIE_EXPIRE_SECONDS);

        String clientParam = request.getParameter("client");
        if (clientParam != null && !clientParam.isBlank()) {
            addCookie(response, request, CLIENT_PARAM_COOKIE_NAME, clientParam.trim(), COOKIE_EXPIRE_SECONDS);
        }
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request,
                                                                 HttpServletResponse response) {
        OAuth2AuthorizationRequest authorizationRequest = loadAuthorizationRequest(request);
        deleteCookie(response, request, AUTH_REQUEST_COOKIE_NAME);
        deleteCookie(response, request, CLIENT_PARAM_COOKIE_NAME);
        return authorizationRequest;
    }

    public String resolveClientParam(HttpServletRequest request) {
        return getCookie(request, CLIENT_PARAM_COOKIE_NAME)
                .map(Cookie::getValue)
                .orElse(null);
    }

    private Optional<Cookie> getCookie(HttpServletRequest request, String name) {
        if (request == null || request.getCookies() == null) {
            return Optional.empty();
        }
        for (Cookie cookie : request.getCookies()) {
            if (name.equals(cookie.getName())) {
                return Optional.of(cookie);
            }
        }
        return Optional.empty();
    }

    private void addCookie(HttpServletResponse response,
                           HttpServletRequest request,
                           String name,
                           String value,
                           int maxAgeSeconds) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(maxAgeSeconds);
        cookie.setSecure(request != null && request.isSecure());
        response.addCookie(cookie);
        response.addHeader("Set-Cookie", buildSetCookieHeader(cookie, value, maxAgeSeconds));
    }

    private void deleteCookie(HttpServletResponse response, HttpServletRequest request, String name) {
        Cookie cookie = new Cookie(name, "");
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        cookie.setSecure(request != null && request.isSecure());
        response.addCookie(cookie);
        response.addHeader("Set-Cookie", buildSetCookieHeader(cookie, "", 0));
    }

    private String buildSetCookieHeader(Cookie cookie, String value, int maxAgeSeconds) {
        StringBuilder header = new StringBuilder();
        header.append(cookie.getName()).append("=").append(value)
                .append("; Max-Age=").append(maxAgeSeconds)
                .append("; Path=/");
        if (cookie.getSecure()) {
            header.append("; Secure");
        }
        header.append("; HttpOnly");
        header.append("; SameSite=").append(SAME_SITE);
        return header.toString();
    }

    private String serialize(OAuth2AuthorizationRequest authorizationRequest) {
        byte[] bytes = SerializationUtils.serialize(authorizationRequest);
        if (bytes == null) {
            throw new IllegalStateException("Failed to serialize OAuth2AuthorizationRequest");
        }
        return Base64.getUrlEncoder().encodeToString(bytes);
    }

    private OAuth2AuthorizationRequest deserialize(String value) {
        byte[] decoded = Base64.getUrlDecoder().decode(value);
        Object deserialized = SerializationUtils.deserialize(decoded);
        if (deserialized instanceof OAuth2AuthorizationRequest request) {
            return request;
        }
        return null;
    }
}
