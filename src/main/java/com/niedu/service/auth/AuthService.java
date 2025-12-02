package com.niedu.service.auth;

import com.niedu.entity.user.User;
import com.niedu.repository.user.UserRepository;
import com.niedu.security.jwt.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public User getUserFromRequest(HttpServletRequest request) {
        // 1. SecurityContext에 인증이 있고 dummy 유저라면 DB에서 가져와 리턴
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserDetails userDetails) {
            if (userDetails.getUsername().equals("1")) { // dummy user
                return userRepository.findById(1L)
                        .orElseThrow(() -> new RuntimeException("Dummy user not found"));
            }
        }

        String token = Arrays.stream(request.getCookies())
                .filter(c -> c.getName().equals("accessToken"))
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow(() -> new RuntimeException("Access token not found"));

        Long userId = Long.parseLong(jwtUtil.extractUsername(token));
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

}
