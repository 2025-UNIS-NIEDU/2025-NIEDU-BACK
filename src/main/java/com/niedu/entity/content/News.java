package com.niedu.entity.content;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "news")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "news_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(name = "original_url", length = 2048)
    private String originalUrl;

    @Column(name = "published_at")
    private LocalDate publishedAt;

    @Lob
    private String summary;

    @Column(name = "infographic_url", length = 2048)
    private String infographicUrl;

    @Lob
    @Column(name = "issue_description")
    private String issueDescription;
}