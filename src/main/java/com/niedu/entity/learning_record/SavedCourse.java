package com.niedu.entity.learning_record;

import com.niedu.entity.user.User;
import com.niedu.entity.course.Course;
import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;

@Entity
@Table(name = "saved_courses")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SavedCourse {

    @EmbeddedId
    private SavedCourseId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("courseId")
    @JoinColumn(name = "course_id")
    private Course course;

    public SavedCourse(User user, Course course) {
        this.id = new SavedCourseId(user.getId(), course.getId());
        this.user = user;
        this.course = course;
    }
}