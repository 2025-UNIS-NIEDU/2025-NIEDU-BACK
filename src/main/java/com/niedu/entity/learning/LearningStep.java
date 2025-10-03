package com.niedu.entity.learning;

import com.niedu.entity.course.Course;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "learning_steps")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LearningStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "step_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "step_order", nullable = false)
    private Integer stepOrder; // 코스 내 단계 순서

    @Column(name = "step_type", nullable = false)
    private String stepType; // 예: 'QUIZ', 'REFLECTION', 'SUMMARY' 등

    @Column(name = "content_id", nullable = false)
    private Long contentId; // 각 stepType에 맞는 콘텐츠 테이블(quizzes, reflections 등)의 PK
}