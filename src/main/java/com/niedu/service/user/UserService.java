package com.niedu.service.user;

import com.niedu.dto.user.UserInfoResponse;
import com.niedu.entity.user.User;
import com.niedu.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    public UserInfoResponse getMyInfo(User user) {
        return UserInfoResponse.builder()
                .nickname(user.getNickname())
                .profileImageUrl(user.getProfileImageUrl())
                .build();
    }
}
