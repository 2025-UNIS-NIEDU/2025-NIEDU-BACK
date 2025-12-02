package com.niedu.entity.content;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@Entity
@DiscriminatorValue("MULTI")
@SuperBuilder
public class MultipleChoiceQuiz extends Content {
    @Column(nullable = false)
    private String question;

    @Column(nullable = false)
    private String correctAnswer;

    @Column(nullable = false)
    private String answerExplanation;

    @Column(nullable = false)
    private String optionA;

    @Column(nullable = false)
    private String optionB;

    @Column(nullable = false)
    private String optionC;

    @Column(nullable = false)
    private String optionD;
}