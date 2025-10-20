package com.niedu.entity.content.quiz;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@DiscriminatorValue("COMPLETION")
public class SentenceCompletionQuiz extends Quiz {
}
