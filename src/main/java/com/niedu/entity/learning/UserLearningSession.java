package com.niedu.entity.learning;

import com.niedu.entity.user.User;
import com.niedu.entity.course.Course;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_learning_sessions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserLearningSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_session_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SessionStatus status;

    @Column
    private Integer progress; // 진행률 (0-100)

    @Column(name = "studied_at")
    private LocalDateTime studiedAt;
}