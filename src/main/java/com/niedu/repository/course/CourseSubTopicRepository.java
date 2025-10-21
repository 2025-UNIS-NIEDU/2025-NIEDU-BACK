package com.niedu.repository.course;

import com.niedu.entity.course.CourseSubTopic;
import com.niedu.entity.course.CourseSubTopicId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseSubTopicRepository extends JpaRepository<CourseSubTopic, CourseSubTopicId> {
    CourseSubTopic findFirstByCourse_Id(Long id);
}