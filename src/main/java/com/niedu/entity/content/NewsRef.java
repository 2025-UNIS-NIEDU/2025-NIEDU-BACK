package com.niedu.entity.content;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "news_ref")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NewsRef {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "news_ref_id")
    private Long id;

    @Column(name = "rag_uuid", unique = true, nullable = false)
    private String ragUuid;

    @Column(nullable = false)
    private String headline;

    @Column
    private String publisher;

    @Column
    private String topic;

    @Column(name = "published_at")
    private LocalDate publishedAt;

    @Column(name = "thumbnail_url", length = 2048)
    private String thumbnailUrl;

    @Column(name = "source_url", length = 2048)
    private String sourceUrl;
}