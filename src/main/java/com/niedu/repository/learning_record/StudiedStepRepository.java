package com.niedu.repository.learning_record;

import com.niedu.entity.learning_record.StudiedStep;
import com.niedu.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudiedStepRepository extends JpaRepository<StudiedStep, Long> {
    List<StudiedStep> findAllByUser_IdAndSession_Id(Long id, Long sessionId);

    Optional<StudiedStep> findFirstByUserAndStep_Session_IdAndIsCompletedFalseOrderByStep_IdAsc(
            User user, Long sessionId
    );

    StudiedStep findFirstByUserAndStep_Session_Id(User user, Long sessionId);

    StudiedStep findByUserIdAndStepId(Long userId, Long stepId);
}
