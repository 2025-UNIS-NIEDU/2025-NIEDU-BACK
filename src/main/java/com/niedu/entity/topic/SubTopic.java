package com.niedu.entity.topic;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sub_topics")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SubTopic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sub_topic_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;

    @Column(nullable = false, length = 50)
    private String name;

    public SubTopic(Topic topic, String name) {
        this.topic = topic;
        this.name = name;
    }
}