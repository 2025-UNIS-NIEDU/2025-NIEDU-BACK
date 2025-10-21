package com.niedu.repository.learning_record;

import com.niedu.entity.learning_record.StudiedCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.*;

@Repository
public interface StudiedCourseRepository extends JpaRepository<StudiedCourse, Long> {
    StudiedCourse findByUser_IdAndCourse_Id(Long id, Long id1);
}
