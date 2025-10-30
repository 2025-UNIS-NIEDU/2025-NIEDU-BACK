package com.niedu.repository.learning_record;

import com.niedu.entity.course.Step;
import com.niedu.entity.learning_record.StudiedStep;
import com.niedu.entity.learning_record.user_answer.UserAnswer;
import com.niedu.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserAnswerRepository extends JpaRepository<UserAnswer, Long>, JpaSpecificationExecutor<UserAnswer> { // --- JpaSpecificationExecutor 추가 ---

    List<UserAnswer> findAllByStudiedStep(StudiedStep studiedStep);

    @Query("""
        SELECT ua
        FROM UserAnswer ua
        JOIN ua.studiedStep ss
        JOIN ss.user u
        JOIN ss.step step
        JOIN step.session s
        JOIN StudiedSession s_session ON s_session.user = u AND s_session.session = s
        LEFT JOIN SentenceCompletionAnswer sca ON ua.id = sca.id
        WHERE u.id = :userId
        AND s_session.startTime BETWEEN :start AND :end
        AND (ua.isCorrect = false OR sca.AIScore < 80)
    """)
    List<UserAnswer> findUserAnswersByDateRange(
            @Param("userId") Long userId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}