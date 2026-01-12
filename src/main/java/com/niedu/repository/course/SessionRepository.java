package com.niedu.repository.course;

import com.niedu.entity.course.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
    List<Session> findAllByCourse_Id(Long courseId);

    @Query(
            value = """
            SELECT * FROM sessions
            WHERE session_id IN (
                SELECT session_id FROM sessions
                ORDER BY session_id DESC
                LIMIT 10
            )
            ORDER BY RANDOM()
            LIMIT 2
            """,
            nativeQuery = true
    )
    List<Session> findTwoRandomFromLatestSessions();
}
