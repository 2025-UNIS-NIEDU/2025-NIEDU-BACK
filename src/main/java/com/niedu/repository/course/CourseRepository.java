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

    @Query(
        value = """
            SELECT DISTINCT c
            FROM Course c
            LEFT JOIN c.topic t
            LEFT JOIN CourseSubTag cst ON cst.course = c
            LEFT JOIN Session s ON s.course = c
            LEFT JOIN s.newsRef nr
            WHERE
                lower(c.title) LIKE lower(concat('%', :keyword, '%'))
                OR lower(c.description) LIKE lower(concat('%', :keyword, '%'))
                OR lower(t.name) LIKE lower(concat('%', :keyword, '%'))
                OR lower(cst.tag) LIKE lower(concat('%', :keyword, '%'))
                OR lower(nr.headline) LIKE lower(concat('%', :keyword, '%'))
            ORDER BY c.createdAt DESC
            """,
        countQuery = """
            SELECT COUNT(DISTINCT c)
            FROM Course c
            LEFT JOIN c.topic t
            LEFT JOIN CourseSubTag cst ON cst.course = c
            LEFT JOIN Session s ON s.course = c
            LEFT JOIN s.newsRef nr
            WHERE
                lower(c.title) LIKE lower(concat('%', :keyword, '%'))
                OR lower(c.description) LIKE lower(concat('%', :keyword, '%'))
                OR lower(t.name) LIKE lower(concat('%', :keyword, '%'))
                OR lower(cst.tag) LIKE lower(concat('%', :keyword, '%'))
                OR lower(nr.headline) LIKE lower(concat('%', :keyword, '%'))
            """
    )
    Page<Course> searchByKeywordOrderByCreatedAtDesc(@Param("keyword") String keyword, Pageable pageable);

    @Query(
        value = """
            SELECT DISTINCT c
            FROM Course c
            LEFT JOIN c.topic t
            LEFT JOIN CourseSubTag cst ON cst.course = c
            LEFT JOIN Session s ON s.course = c
            LEFT JOIN s.newsRef nr
            WHERE
                lower(c.title) LIKE lower(concat('%', :keyword, '%'))
                OR lower(c.description) LIKE lower(concat('%', :keyword, '%'))
                OR lower(t.name) LIKE lower(concat('%', :keyword, '%'))
                OR lower(cst.tag) LIKE lower(concat('%', :keyword, '%'))
                OR lower(nr.headline) LIKE lower(concat('%', :keyword, '%'))
            ORDER BY c.viewCount DESC
            """,
        countQuery = """
            SELECT COUNT(DISTINCT c)
            FROM Course c
            LEFT JOIN c.topic t
            LEFT JOIN CourseSubTag cst ON cst.course = c
            LEFT JOIN Session s ON s.course = c
            LEFT JOIN s.newsRef nr
            WHERE
                lower(c.title) LIKE lower(concat('%', :keyword, '%'))
                OR lower(c.description) LIKE lower(concat('%', :keyword, '%'))
                OR lower(t.name) LIKE lower(concat('%', :keyword, '%'))
                OR lower(cst.tag) LIKE lower(concat('%', :keyword, '%'))
                OR lower(nr.headline) LIKE lower(concat('%', :keyword, '%'))
            """
    )
    Page<Course> searchByKeywordOrderByViewCountDesc(@Param("keyword") String keyword, Pageable pageable);
}
