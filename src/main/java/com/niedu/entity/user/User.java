package com.niedu.entity.user;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder; // Builder를 사용하기 위해 import 합니다.
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "kakao_id", unique = true, nullable = false)
    private String kakaoId;

    @Column(nullable = false, length = 50)
    private String nickname;

    @Column
    private String email;

    @Column(name = "profile_image_url", length = 2048)
    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "subscription_tier", nullable = false)
    private SubscriptionTier subscriptionTier;

    @Column(name = "push_notification_enabled", nullable = false)
    private boolean pushNotificationEnabled;

    @Column(name = "push_enabled_morning", nullable = false)
    private boolean pushEnabledMorning;

    @Column(name = "push_enabled_evening", nullable = false)
    private boolean pushEnabledEvening;

    @Column(name = "attendance_streak", nullable = false, columnDefinition = "INT DEFAULT 0")
    private int attendanceStreak;

    @Column(name = "last_attended_date")
    private LocalDate lastAttendedDate;

    @Column(name = "trial_started_at")
    private LocalDateTime trialStartedAt;

    @Column(name = "trial_ends_at")
    private LocalDateTime trialEndsAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "withdrawn_at")
    private LocalDateTime withdrawnAt;

    @Builder
    public User(String kakaoId, String nickname, String email, String profileImageUrl) {
        this.kakaoId = kakaoId;
        this.nickname = nickname;
        this.email = email;
        this.profileImageUrl = profileImageUrl;
    }

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.status = UserStatus.ACTIVE;
        this.subscriptionTier = SubscriptionTier.FREE;
        this.pushNotificationEnabled = true;
        this.pushEnabledMorning = true; // 기본값 ON
        this.pushEnabledEvening = true; // 기본값 ON
        this.attendanceStreak = 0;
    }
}