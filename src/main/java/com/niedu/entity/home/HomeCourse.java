package com.niedu.entity.home;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "home_course")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HomeCourse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String thumbnailUrl;
    private String title;

    @Column(length = 2000)
    private String longDescription;

    private String topic;

    @Column(nullable = false)
    private String type;

    private Long userId;
}
