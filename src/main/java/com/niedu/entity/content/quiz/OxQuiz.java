package com.niedu.entity.content.quiz;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@DiscriminatorValue("OX")
public class OxQuiz extends Quiz {

    @Column(nullable = false)
    private boolean correctAnswer;

    @Lob
    @Column(nullable = false)
    private String answerExplanation;
}