package com.niedu.entity.home;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "home_news")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HomeNews {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String thumbnailUrl;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String publisher;

    @Column(nullable = false)
    private String topic;

    @Column(nullable = false)
    private LocalDate publishedDate; // YYYY-MM-DD, 오전 8시 기준 갱신용
}
