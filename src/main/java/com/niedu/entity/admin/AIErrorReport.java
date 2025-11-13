package com.niedu.entity.admin;

import com.niedu.entity.course.Course;
import com.niedu.entity.course.Session;
import com.niedu.entity.course.Step;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ai_error_report")
@NoArgsConstructor
@AllArgsConstructor
public class AIErrorReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ai_error_report_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "step_id", nullable = false)
    private Step step;

    @Column(name = "content_id", nullable = false)
    private Long contentId;
}