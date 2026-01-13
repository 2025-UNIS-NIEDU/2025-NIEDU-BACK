package com.niedu.repository.course;

import com.niedu.entity.course.Step;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StepRepository extends JpaRepository<Step, Long> {
    List<Step> findAllBySession_Id(Long sessionId);

    List<Step> findAllBySession_IdOrderByStepOrderAsc(Long sessionId);
}
