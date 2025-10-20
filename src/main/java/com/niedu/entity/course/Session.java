package com.niedu.entity.course;

import com.niedu.entity.content.NewsRef;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "sessions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "session_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "news_ref_id", nullable = false, unique = true)
    private NewsRef newsRef;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Level level;

    @Column(name = "news_published_at")
    private LocalDate newsPublishedAt;
}