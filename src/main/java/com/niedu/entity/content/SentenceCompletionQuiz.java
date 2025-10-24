package com.niedu.entity.content;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@DiscriminatorValue("COMPLETION")
public class SentenceCompletionQuiz extends Content {
    @Column(nullable = false)
    private String question;

    @Column(nullable = false)
    private Integer questionIndex;  // 1, 2, 3 중 하나
}
