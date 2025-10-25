package com.niedu.repository.learning_record;

import com.niedu.entity.learning_record.SavedCourse;
import com.niedu.entity.learning_record.SavedCourseId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SavedCourseRepository extends JpaRepository<SavedCourse, SavedCourseId> {
}
