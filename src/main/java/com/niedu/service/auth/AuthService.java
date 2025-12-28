package com.niedu.service.auth;

import com.niedu.entity.user.User;
import com.niedu.repository.user.UserRepository;
import com.niedu.security.CookieUtils;
import com.niedu.security.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public User getUserFromRequest(HttpServletRequest request) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserDetails userDetails) {
            Long userId = Long.parseLong(userDetails.getUsername());
            return userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
        }

        String token = CookieUtils.getCookieValue(request, "accessToken")
                .orElseThrow(() -> new RuntimeException("Access token not found"));

        Long userId = Long.parseLong(jwtUtil.extractUsername(token));
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

}
