package com.niedu.entity.admin;

import com.niedu.entity.course.Course;
import com.niedu.entity.course.Session;
import com.niedu.entity.course.Step;
import jakarta.persistence.*;

@Entity
@Table(name = "ai_error_report")
public class AIErrorReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ai_error_report_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private Session session;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "step_id", nullable = false)
    private Step step;
}
