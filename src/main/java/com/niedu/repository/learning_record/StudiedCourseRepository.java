package com.niedu.repository.learning_record;

import com.niedu.entity.learning_record.StudiedCourse;
import com.niedu.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.*;

@Repository
public interface StudiedCourseRepository extends JpaRepository<StudiedCourse, Long> {
    StudiedCourse findByUserAndCourse_Id(User user, Long courseId);
}
