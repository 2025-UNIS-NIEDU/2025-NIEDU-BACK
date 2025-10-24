package com.niedu.entity.course;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CourseSubTopicId implements Serializable {
    @Column(name = "course_id")
    private Long courseId;
    @Column(name = "sub_topic_id")
    private Long subTopicId;
}
