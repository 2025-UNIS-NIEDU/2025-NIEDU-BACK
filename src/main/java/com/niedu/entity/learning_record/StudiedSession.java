package com.niedu.entity.learning_record;

import com.niedu.entity.course.Session;
import com.niedu.entity.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Table(name = "studied_sessions")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class StudiedSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private Session session;

    @Column(nullable = false)
    private Float progress;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SessionStatus status;

    @Builder.Default
    @Column(name = "studied_time")
    private Duration studiedTime = Duration.ZERO;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @Transient
    public Duration getCurrentDuration() {
        if (startTime == null || endTime == null) return Duration.ZERO;
        return Duration.between(startTime, endTime);
    }
}