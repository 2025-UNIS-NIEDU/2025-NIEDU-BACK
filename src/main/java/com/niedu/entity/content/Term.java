package com.niedu.entity.content;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "terms")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Term {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "term_id")
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Lob
    @Column(nullable = false)
    private String description;

    @Column(name = "article_context", length = 500)
    private String articleContext;

    @Lob
    @Column(name = "additional_explanation")
    private String additionalExplanation;
}