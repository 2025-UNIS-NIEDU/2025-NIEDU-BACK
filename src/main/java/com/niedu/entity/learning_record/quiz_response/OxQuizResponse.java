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
@DiscriminatorValue("OX")
@Table(name = "quiz_response_ox")
class OxQuizResponse extends QuizResponse {
    // OX형은 추가 필드 불필요 — 기본 필드로 충분
}
