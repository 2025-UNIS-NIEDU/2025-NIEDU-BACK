package com.niedu.security.service;

import com.niedu.entity.user.User;
import com.niedu.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * OAuth + JWT 기반 인증에서 사용되는 최소 구현체.
 * - DB의 User ID(subject)를 기반으로 UserDetails 생성
 * - 비밀번호는 검증하지 않음
 * - ROLE_USER 기본 권한 부여
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // JWT subject는 userId(Long)로 설정되어 있으므로 Long 변환
        Long userId = Long.parseLong(username);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));

        // password는 OAuth 기반 서비스이므로 사용하지 않음
        return org.springframework.security.core.userdetails.User.builder()
                .username(String.valueOf(user.getId()))
                .password("") // 비밀번호 검증 안 함
                .authorities("ROLE_USER")
                .build();
    }
}
