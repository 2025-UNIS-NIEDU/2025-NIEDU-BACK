package com.niedu.entity.course;

import com.niedu.entity.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
class FavoriteCourseId implements Serializable {
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "course_id")
    private Long courseId;
}

@Entity
@Table(name = "favorite_courses")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FavoriteCourse {

    @EmbeddedId
    private FavoriteCourseId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("courseId")
    @JoinColumn(name = "course_id")
    private Course course;

    public FavoriteCourse(User user, Course course) {
        this.id = new FavoriteCourseId(user.getId(), course.getId());
        this.user = user;
        this.course = course;
    }
}