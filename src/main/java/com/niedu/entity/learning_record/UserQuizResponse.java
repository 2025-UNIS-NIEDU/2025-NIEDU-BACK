package com.niedu.entity.learning_record;

import com.niedu.entity.user.User;
import com.niedu.entity.content.quiz.Quiz;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

enum QuizAnswerStatus {
    UNANSWERED, // 미응답
    CORRECT,    // 정답
    INCORRECT   // 오답
}

@Entity
@Table(name = "user_quiz_responses")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserQuizResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "response_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuizAnswerStatus status;

    @Setter
    @Column(name = "submitted_answer", nullable = true)
    private String submittedAnswer;

    @Setter
    @Column(name = "submitted_at", nullable = true)
    private LocalDateTime submittedAt;


    public UserQuizResponse(User user, Quiz quiz) {
        this.user = user;
        this.quiz = quiz;
        this.status = QuizAnswerStatus.UNANSWERED;
    }
}