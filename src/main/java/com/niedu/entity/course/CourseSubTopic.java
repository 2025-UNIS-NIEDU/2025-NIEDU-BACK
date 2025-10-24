package com.niedu.entity.course;

import com.niedu.entity.topic.SubTopic;
import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;

@Entity
@Table(name = "course_sub_topics")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CourseSubTopic {

    @EmbeddedId
    private CourseSubTopicId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("courseId")
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("subTopicId")
    @JoinColumn(name = "sub_topic_id")
    private SubTopic subTopic;

    public CourseSubTopic(Course course, SubTopic subTopic) {
        this.id = new CourseSubTopicId(course.getId(), subTopic.getId());
        this.course = course;
        this.subTopic = subTopic;
    }
}