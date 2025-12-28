package com.niedu.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public final class TokenCookieSupport {

    private TokenCookieSupport() {
    }

    public static boolean isLocalRequest(HttpServletRequest request) {
        if (request == null) {
            return false;
        }
        String serverName = request.getServerName();
        return "localhost".equalsIgnoreCase(serverName) || "127.0.0.1".equals(serverName);
    }

    public static void addTokenCookie(HttpServletResponse response,
                                      String name,
                                      String value,
                                      int maxAgeSeconds,
                                      String domain,
                                      boolean secure,
                                      String sameSite) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(secure);
        cookie.setPath("/");
        cookie.setMaxAge(maxAgeSeconds);
        if (domain != null && !domain.isBlank()) {
            cookie.setDomain(domain);
        }
        response.addCookie(cookie);

        response.addHeader("Set-Cookie", buildSetCookieHeader(name, value, maxAgeSeconds, domain, secure, sameSite));
    }

    private static String buildSetCookieHeader(String name,
                                               String value,
                                               int maxAgeSeconds,
                                               String domain,
                                               boolean secure,
                                               String sameSite) {
        StringBuilder header = new StringBuilder();
        header.append(name).append("=").append(value)
                .append("; Max-Age=").append(maxAgeSeconds)
                .append("; Path=/");
        if (domain != null && !domain.isBlank()) {
            header.append("; Domain=").append(domain);
        }
        if (secure) {
            header.append("; Secure");
        }
        header.append("; HttpOnly");
        if (sameSite != null && !sameSite.isBlank()) {
            header.append("; SameSite=").append(sameSite);
        }
        return header.toString();
    }
}
