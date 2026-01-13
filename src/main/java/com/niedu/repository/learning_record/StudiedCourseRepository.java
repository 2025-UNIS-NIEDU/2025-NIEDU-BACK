package com.niedu.repository.learning_record;

import com.niedu.entity.learning_record.StudiedCourse;
import com.niedu.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudiedCourseRepository extends JpaRepository<StudiedCourse, Long> {
    StudiedCourse findByUserAndCourse_Id(User user, Long courseId);

    // 홈 화면 최근 학습 코스 조회를 위한 메서드
    Page<StudiedCourse> findByUserOrderByUpdatedAtDesc(User user, Pageable pageable);
    // 사용자가 학습 중인 모든 세션 목록 조회 시 활용 가능
    List<StudiedCourse> findAllByUserAndCourse_Id(User user, Long courseId);
}