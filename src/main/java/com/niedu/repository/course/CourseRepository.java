package com.niedu.repository.course;

import com.niedu.entity.course.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    // --- 추가된 검색 및 학습 이력 메서드 ---

    /**
     * 검색 범위 확장: 제목, 토픽명, 태그, 뉴스 헤드라인(연관된 경우) 포함 - 최신순
     * NewsRef와 Course의 관계를 ID 매칭으로 처리하여 필드명 오류 방지
     */
    @Query("""
        SELECT DISTINCT c FROM Course c
        LEFT JOIN c.topic t
        LEFT JOIN CourseSubTag cst ON cst.course = c
        LEFT JOIN NewsRef n ON n.courseId = c.id
        WHERE LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(t.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(cst.tag) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(n.headline) LIKE LOWER(CONCAT('%', :keyword, '%'))
        ORDER BY c.createdAt DESC
    """)
    Page<Course> searchByComplexCriteria(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 검색 범위 확장: 제목, 토픽명, 태그, 뉴스 헤드라인(연관된 경우) 포함 - 인기순
     */
    @Query("""
        SELECT DISTINCT c FROM Course c
        LEFT JOIN c.topic t
        LEFT JOIN CourseSubTag cst ON cst.course = c
        LEFT JOIN NewsRef n ON n.courseId = c.id
        WHERE LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(t.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(cst.tag) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(n.headline) LIKE LOWER(CONCAT('%', :keyword, '%'))
        ORDER BY c.viewCount DESC
    """)
    Page<Course> searchByComplexCriteriaOrderByPopular(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 유저가 실제 학습 중인 코스 가져오기 (홈 화면 '최근 학습한 코스'용)
     * StudiedCourse 엔티티에 id 필드가 존재해야 하며,
     * BaseEntity 등을 상속받아 createdAt 또는 updatedAt이 있는 상태여야 합니다.
     */
    @Query("""
        SELECT sc.course FROM StudiedCourse sc
        WHERE sc.user.id = :userId
        ORDER BY sc.id DESC
    """)
    List<Course> findRecentStudiedCourses(@Param("userId") Long userId, Pageable pageable);
}
