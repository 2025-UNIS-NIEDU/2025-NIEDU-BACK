package com.niedu.entity.learning_record;

import com.niedu.entity.course.Session;
import com.niedu.entity.course.Step;
import com.niedu.entity.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "shared_answer")
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class SharedResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "step_id")
    private Step step;

    @Column(length = 200)
    private String userResponse;

    private LocalDateTime createdAt;
}