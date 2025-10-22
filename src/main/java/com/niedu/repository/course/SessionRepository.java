package com.niedu.repository.course;

import com.niedu.entity.course.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
    List<Session> findAllByCourse_Id(Long courseId);
}
