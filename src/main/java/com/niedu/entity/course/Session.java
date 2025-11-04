package com.niedu.entity.course;

import com.niedu.entity.content.NewsRef;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sessions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
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
}