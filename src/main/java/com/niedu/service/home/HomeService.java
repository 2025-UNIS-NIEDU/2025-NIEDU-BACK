package com.niedu.service.home;

import com.niedu.dto.home.HomeCourseRecord;
import com.niedu.dto.home.HomeNewsRecord;
import com.niedu.entity.course.Course;
import com.niedu.entity.course.Session;
import com.niedu.entity.user.User;
import com.niedu.repository.course.CourseRepository;
import com.niedu.repository.course.SessionRepository;
import com.niedu.repository.learning_record.SavedCourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HomeService {

    private final CourseRepository courseRepository;
    private final SavedCourseRepository savedCourseRepository;
    private final SessionRepository sessionRepository;

    public List<HomeNewsRecord> getRandomNews(User user) {
        List<Session> sessions = sessionRepository.findTwoRandomFromLatestSessions();

        List<HomeNewsRecord> responses = sessions.stream()
                .map(HomeNewsRecord::fromEntity)
                .toList();

        if (responses == null || responses.isEmpty())
            throw new RuntimeException("최신 세션이 없습니다.");

        return responses;
    }

    public List<HomeCourseRecord> getCourses(User user, String type, String view) {
        int limit = "preview".equalsIgnoreCase(view) ? 6 : 10;
        Pageable pageable = PageRequest.of(0, limit);

        Page<Course> courses;

        if ("recent".equalsIgnoreCase(type)) {
            courses = courseRepository.findAllByOrderByCreatedAtDesc(pageable);
        } else if ("saved".equalsIgnoreCase(type)) {
            courses = savedCourseRepository.findByUserOrderByCourse_CreatedAtDesc(user, pageable);
        } else {
            throw new IllegalArgumentException("타입 파라미터 오류: " + type);
        }

        return courses.stream()
                .map(HomeCourseRecord::fromEntity)
                .toList();
    }
}
