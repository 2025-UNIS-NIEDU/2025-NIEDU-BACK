package com.niedu.entity.learning_record.quiz_response;

import com.niedu.entity.content.quiz.Quiz;
import com.niedu.entity.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 🔹 부모 엔티티 (공통 필드)
 * - JOINED 전략: 퀴즈 유형별로 하위 테이블 분리
 * - DiscriminatorColumn 으로 quiz_type 구분
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "quiz_type")
@Table(name = "quiz_response")
public abstract class QuizResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 사용자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // 퀴즈
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    // 사용자 답변 (공통)
    @Column(nullable = false)
    private String userAnswer;

    // 채점 및 상태
    @Column
    private Boolean isCorrect;

    @Column
    private Integer score;

    // 생성일자
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}