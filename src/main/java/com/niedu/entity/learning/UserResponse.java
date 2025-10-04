package com.niedu.entity.learning;

import com.niedu.entity.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_responses")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "response_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private LearningSession learningSession;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "step_id", nullable = false)
    private LearningStep learningStep;

    @Lob
    @Column(name = "submitted_answer")
    private String submittedAnswer; // 서술형 답변 등

    @Column(name = "selected_option_id")
    private Long selectedOptionId; // 객관식 선택 답변의 PK

    @Column(name = "is_correct")
    private Boolean isCorrect;

    @Column(name = "submitted_at", nullable = false, updatable = false)
    private LocalDateTime submittedAt;

    @Builder
    public UserResponse(LearningSession learningSession, User user, LearningStep learningStep, String submittedAnswer, Long selectedOptionId, Boolean isCorrect) {
        this.learningSession = learningSession;
        this.user = user;
        this.learningStep = learningStep;
        this.submittedAnswer = submittedAnswer;
        this.selectedOptionId = selectedOptionId;
        this.isCorrect = isCorrect;
    }

    @PrePersist
    protected void onSubmit() {
        this.submittedAt = LocalDateTime.now();
    }
}