package com.niedu.repository.learning_record;


import com.niedu.entity.learning_record.StudiedSession;
import com.niedu.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StudiedSessionRepository extends JpaRepository<StudiedSession, Long> {

    StudiedSession findByUserAndSession_Id(User user, Long sessionId);

    List<StudiedSession> findByUserAndStartTimeBetween(User user, LocalDateTime start, LocalDateTime end);
}