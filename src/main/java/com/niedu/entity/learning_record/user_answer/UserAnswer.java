package com.niedu.entity.learning_record.user_answer;

import com.niedu.entity.course.Step;
import com.niedu.entity.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_answers")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "answer_type")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class UserAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_answer_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "step_id", nullable = false)
    private Step step;

    @Column(name = "is_correct")
    private Boolean isCorrect;

    @Column(name = "score")
    private Float score;

    protected UserAnswer(User user, Step step) {
        this.user = user;
        this.step = step;
    }
}
