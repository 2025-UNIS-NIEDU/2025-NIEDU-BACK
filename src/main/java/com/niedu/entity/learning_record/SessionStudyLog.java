package com.niedu.entity.learning_record;

import jakarta.persistence.*;

import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Table(name = "session_study_log")
public class SessionStudyLog {

    @Id
    @GeneratedValue
    private Long id;

    private Long userId;
    private Long sessionId;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @Transient
    public Duration getDuration() {
        return Duration.between(startTime, endTime);
    }
}
