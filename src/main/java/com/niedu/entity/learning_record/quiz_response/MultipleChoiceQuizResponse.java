package com.niedu.entity.learning_record.quiz_response;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@DiscriminatorValue("MULTI")
@Table(name = "quiz_response_multi")
public class MultipleChoiceQuizResponse extends QuizResponse {

    @Column(length = 1)
    private String selectedOption; // 사용자가 선택한 보기 (A/B/C/D)
}
