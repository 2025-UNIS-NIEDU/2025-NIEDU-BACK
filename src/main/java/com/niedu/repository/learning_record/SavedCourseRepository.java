package com.niedu.repository.learning_record;

import com.niedu.entity.course.Course;
import com.niedu.entity.learning_record.SavedCourse;
import com.niedu.entity.learning_record.SavedCourseId;
import com.niedu.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SavedCourseRepository extends JpaRepository<SavedCourse, SavedCourseId> {
    Page<Course> findByUserOrderByCourse_CreatedAtDesc(User user, Pageable pageable);

}
