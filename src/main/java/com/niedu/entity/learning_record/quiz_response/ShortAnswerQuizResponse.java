package com.niedu.entity.learning_record.quiz_response;

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
@DiscriminatorValue("SHORT")
@Table(name = "quiz_response_short")
public class ShortAnswerQuizResponse extends QuizResponse {
    // 별도 필드 없음 — 기본 구조 그대로 사용
}