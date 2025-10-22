package com.niedu.repository.learning_record;

import com.niedu.entity.learning_record.StudiedStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudiedStepRepository extends JpaRepository<StudiedStep, Long> {
}
