package com.niedu.entity.course;

import com.niedu.entity.content.Content;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "steps")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Step {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "step_id")
    private Long id;

    // 어떤 세션에 속해 있는지 N:1 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private Session session;

    // 세션 내에서의 순서
    @Column(name = "step_order", nullable = false)
    private Integer stepOrder;

    // 이 단계의 유형 (요약 읽기, OX 퀴즈 등)
    @Enumerated(EnumType.STRING)
    @Column(name = "step_type", nullable = false)
    private StepType type;

    // 난이도 레벨 (N/I/E)
    @Enumerated(EnumType.STRING)
    @Column(name = "level", nullable = false)
    private Level level;
}
