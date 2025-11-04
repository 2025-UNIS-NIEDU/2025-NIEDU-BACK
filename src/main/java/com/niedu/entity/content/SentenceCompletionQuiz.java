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
@DiscriminatorValue("COMPLETION")
@SuperBuilder
public class SentenceCompletionQuiz extends Content {
    @Column(nullable = false)
    private String question;
}
