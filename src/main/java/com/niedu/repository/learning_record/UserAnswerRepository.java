package com.niedu.repository.learning_record;

import com.niedu.entity.learning_record.StudiedStep;
import com.niedu.entity.learning_record.user_answer.UserAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserAnswerRepository extends JpaRepository<UserAnswer, Long>, JpaSpecificationExecutor<UserAnswer> {

    List<UserAnswer> findAllByStudiedStep(StudiedStep studiedStep);

    // 마이페이지 복습 노트용: 틀린 퀴즈 목록 조회
    @Query("""
        SELECT ua
        FROM UserAnswer ua
        JOIN ua.studiedStep ss
        JOIN ss.step step
        JOIN step.session s
        JOIN StudiedSession s_session ON s_session.session = s AND s_session.user = ss.user
        WHERE ss.user.id = :userId
        AND s_session.startTime BETWEEN :start AND :end
        AND (
            ua.isCorrect = false 
            OR EXISTS (
                SELECT sca FROM SentenceCompletionAnswer sca 
                WHERE sca.id = ua.id AND sca.AIScore < 80
            )
        )
    """)
    List<UserAnswer> findUserAnswersByDateRange(
            @Param("userId") Long userId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}