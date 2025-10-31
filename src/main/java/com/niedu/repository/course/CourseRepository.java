package com.niedu.repository.course;

import com.niedu.entity.course.Course;
import org.springframework.data.domain.Page; // Page import
import org.springframework.data.domain.Pageable; // PageRequest 대신 Pageable import
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByTopic_NameIgnoreCaseOrderByCreatedAtDesc(String topicName, Pageable pageable);

    List<Course> findAllByOrderByViewCountDesc(Pageable pageable);

    @Query("""
        SELECT c
        FROM Course c
        WHERE c.topic IN (
            SELECT utp.topic
            FROM UserTopicPreference utp
            WHERE utp.user.id = :userId
        )
        ORDER BY c.viewCount DESC
        """)
    List<Course> findCoursesByUserPreferredTopicsOrderByViewCountDesc(@Param("userId") Long userId, Pageable pageable);

    @Query("""
    SELECT c
    FROM Course c
    WHERE c.topic NOT IN (
        SELECT utp.topic
        FROM UserTopicPreference utp
        WHERE utp.user.id = :userId
    )
    ORDER BY c.viewCount DESC
    """)
    List<Course> findCoursesByTopicsUserDidNotPreferOrderByViewCountDesc(@Param("userId") Long userId, Pageable pageable);

    @Query("""
    SELECT c
    FROM Course c
    WHERE c.id NOT IN (
        SELECT sc.course.id
        FROM StudiedCourse sc
        WHERE sc.user.id = :userId
    )
    ORDER BY function('random')
    """)
    List<Course> findRandomUnstudiedCoursesByUser(@Param("userId") Long userId, Pageable pageable);


    Page<Course> findAllByOrderByCreatedAtDesc(Pageable pageable);
    Page<Course> findByTitleContainingIgnoreCaseOrderByCreatedAtDesc(String keyword, Pageable pageable);
    Page<Course> findByTitleContainingIgnoreCaseOrderByViewCountDesc(String keyword, Pageable pageable);
}