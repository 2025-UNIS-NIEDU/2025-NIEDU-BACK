package com.niedu.entity.learning_record;

import com.niedu.entity.course.Step;
import com.niedu.entity.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "studied_steps")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class StudiedStep {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "step_id", nullable = false)
    private Step step;

    @Column(nullable = false)
    private Boolean isCompleted;
}
