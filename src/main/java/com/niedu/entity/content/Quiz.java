package com.niedu.entity.content;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "quizzes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Quiz {
    // ... 기존 id, question, feedback 등 필드 ...
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "quiz_id")
    private Long id;

    @Lob
    @Column(nullable = false)
    private String question;

    @Lob
    private String feedback;

    @Lob
    private String answer;
}