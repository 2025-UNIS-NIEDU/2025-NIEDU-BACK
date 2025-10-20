package com.niedu.entity.learning_record;

import com.niedu.entity.user.User;
import com.niedu.entity.content.quiz.Quiz;
import jakarta.persistence.*;
import lombok.*;

enum QuizAnswerStatus {
    UNANSWERED,
    CORRECT,
    INCORRECT
}

@Entity
@Table(name = "quiz_responses")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QuizResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    public QuizResponse(User user, Quiz quiz) {
        this.user = user;
        this.quiz = quiz;
        this.status = QuizAnswerStatus.UNANSWERED;
    }
}