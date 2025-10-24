package com.niedu.repository.home;

import com.niedu.entity.home.HomeCourse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HomeCourseRepository extends JpaRepository<HomeCourse, Long> {

    List<HomeCourse> findByUserIdAndTypeOrderByIdDesc(Long userId, String type);
}
