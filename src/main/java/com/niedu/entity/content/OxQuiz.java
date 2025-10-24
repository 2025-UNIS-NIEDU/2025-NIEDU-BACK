package com.niedu.entity.content;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@DiscriminatorValue("OX")
public class OxQuiz extends Content {
    @Column(nullable = false)
    private String question;

    @Column(nullable = false)
    private String correctAnswer;

    @Lob
    @Column(nullable = false)
    private String answerExplanation;
}