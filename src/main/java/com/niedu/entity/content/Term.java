package com.niedu.entity.content;

import com.niedu.entity.course.Session;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "terms")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Term {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "term_id")
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private Session session;

    @Column(nullable = false, columnDefinition="text")
    private String definition;

    @Column(name = "example_sentence", length = 500)
    private String exampleSentence;

    @Column(name = "additional_explanation", columnDefinition="text")
    private String additionalExplanation;

}