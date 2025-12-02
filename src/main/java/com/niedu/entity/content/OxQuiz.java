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
@DiscriminatorValue("OX")
@SuperBuilder
public class OxQuiz extends Content {
    @Column(nullable = false)
    private String question;

    @Column(nullable = false)
    private String correctAnswer;

    @Column(nullable = false)
    private String answerExplanation;
}