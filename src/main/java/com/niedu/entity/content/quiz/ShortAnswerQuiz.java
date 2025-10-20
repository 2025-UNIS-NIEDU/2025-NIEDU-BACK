package com.niedu.entity.content.quiz;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@DiscriminatorValue("SHORT")
public class ShortAnswerQuiz extends Quiz {

    @Column(nullable = false)
    private String correctAnswer;

    @Lob
    @Column(nullable = false)
    private String answerExplanation;
}