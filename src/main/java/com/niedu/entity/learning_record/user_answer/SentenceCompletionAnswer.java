package com.niedu.entity.learning_record.user_answer;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@DiscriminatorValue("SENTENCE")
public class SentenceCompletionAnswer extends UserAnswer {
    @Column(nullable = false)
    private Integer questionIndex;

    @Column(nullable = false)
    private String userAnswer;

    @Column
    private Integer AIScore;

    @Column
    private String AIFeedback;
}
