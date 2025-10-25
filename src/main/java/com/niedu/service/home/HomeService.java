package com.niedu.service.home;

import com.niedu.dto.home.HomeCourseRecord;
import com.niedu.dto.home.HomeNewsRecord;
import com.niedu.entity.content.NewsRef;
import com.niedu.entity.course.Course;
import com.niedu.repository.content.NewsRefRepository;
import com.niedu.repository.course.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HomeService {

    private final NewsRefRepository newsRefRepository;
    private final CourseRepository courseRepository;

    public HomeNewsRecord getRandomNews() {
        PageRequest pageable = PageRequest.of(0, 1);
        List<NewsRef> newsList = newsRefRepository.findAllByOrderByIdDesc(pageable);

        if (newsList.isEmpty()) {
            return new HomeNewsRecord("","뉴스가 없습니다.","","");
        }

        NewsRef news = newsList.get(0);
        return HomeNewsRecord.fromEntity(news);
    }

    public HomeCourseRecord.CourseListResponse getCourses(Long userId, String type, String view) {
        int limit = "preview".equalsIgnoreCase(view) ? 6 : 10;
        PageRequest pageable = PageRequest.of(0, limit);

        List<Course> courses;

        if ("recent".equalsIgnoreCase(type)) {
            courses = courseRepository.findAllByOrderByCreatedAtDesc(pageable);
        } else if ("saved".equalsIgnoreCase(type)) {
            courses = courseRepository.findAllByOrderByCreatedAtDesc(pageable);
        } else {
            throw new IllegalArgumentException("Invalid type parameter: " + type);
        }

        return HomeCourseRecord.CourseListResponse.fromEntities(courses);
    }
}
