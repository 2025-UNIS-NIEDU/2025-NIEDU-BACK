package com.niedu.repository.course;

import com.niedu.entity.course.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
    List<Session> findAllByCourse_Id(Long courseId);
    long countByCourse_Id(Long courseId);
    Optional<Session> findByNewsRef_Id(Long newsRefId);

    @Query(
            value = """
            SELECT s.* FROM sessions s
            JOIN news_ref n ON s.news_ref_id = n.news_ref_id
            WHERE n.published_at = (
                SELECT MAX(published_at) FROM news_ref
            )
            ORDER BY RANDOM()
            LIMIT 2
            """,
            nativeQuery = true
    )
    List<Session> findTwoRandomFromLatestNewsRefDate();
}
