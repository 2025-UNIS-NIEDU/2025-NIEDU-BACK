package com.niedu.repository.learning_record; // (패키지 경로는 기존과 동일하게)

import com.niedu.entity.course.Step;
import com.niedu.entity.learning_record.SharedResponse;
import com.niedu.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SharedResponseRepository extends JpaRepository<SharedResponse, Long> {

    List<SharedResponse> findAllByStepAndUserNot(Step step, User user);

    List<SharedResponse> findByUserAndCreatedAtBetween(User user, LocalDateTime start, LocalDateTime end);
}