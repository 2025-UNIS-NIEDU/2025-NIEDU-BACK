package com.niedu.entity.content;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@Entity
@DiscriminatorValue("COMPLETION")
@SuperBuilder
public class SentenceCompletionQuiz extends Content {

    @Column(nullable = false)
    private String question;

    @Column(name = "reference_answer", nullable = false)
    private String referenceAnswer;
}
