package com.niedu.repository.course;

import com.niedu.entity.course.CourseSubTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseSubTagRepository extends JpaRepository<CourseSubTag, Integer> {
}
