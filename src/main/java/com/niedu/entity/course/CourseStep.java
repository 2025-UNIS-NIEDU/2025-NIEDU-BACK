package com.niedu.entity.course;

import com.niedu.entity.learning.Step;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "course_steps")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CourseStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "step_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "step_order", nullable = false)
    private Integer stepOrder;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Step step;

    @Column(nullable = false)
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(name = "original_url", length = 2048)
    private String originalUrl;

    @Column(name = "news_published_at")
    private LocalDateTime newsPublishedAt;

    @Column(name = "step_type")
    private String stepType;

    @Column(name = "target_content_id")
    private Long targetContentId;
}