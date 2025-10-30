package com.niedu.repository.learning_record;

import com.niedu.entity.learning_record.StudiedSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StudiedSessionRepository extends JpaRepository<StudiedSession, Long> {

    StudiedSession findByUser_IdAndSession_Id(Long id, Long sessionId);

    List<StudiedSession> findByUserIdAndStartTimeBetween(Long userId, LocalDateTime start, LocalDateTime end);
}