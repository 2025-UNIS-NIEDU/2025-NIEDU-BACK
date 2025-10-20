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
@DiscriminatorValue("COMPLETION")
@Table(name = "quiz_response_completion")
public class SentenceCompletionQuizResponse extends QuizResponse {
    @Column(nullable = false)
    private Integer questionIndex;  // 1, 2, 3 중 하나

    @Column
    private Integer AIScore; // AI가 생성한 점수

    @Column(length = 500)
    private String AIFeedback; // AI가 생성한 피드백 (100자 이내)

    @Column
    private Boolean isReported; // 오류 제보 여부
}