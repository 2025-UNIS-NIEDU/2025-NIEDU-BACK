package com.niedu.service.home;

import com.niedu.dto.attendance.AttendanceStreakResponse;
import com.niedu.dto.home.*;
import com.niedu.entity.home.HomeCourse;
import com.niedu.entity.home.HomeNews;
import com.niedu.repository.home.HomeCourseRepository;
import com.niedu.repository.home.HomeNewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class HomeService {

    private final HomeNewsRepository homeNewsRepository;
    private final HomeCourseRepository homeCourseRepository;
    private static final Random random = new Random();

    public HomeNewsResponse getRandomNews() {
        try {
            List<HomeNews> newsList = homeNewsRepository.findTodayNews();
            if (newsList.isEmpty()) {
                throw new IllegalStateException("오늘의 뉴스가 존재하지 않습니다.");
            }

            HomeNews selected = newsList.get(random.nextInt(newsList.size()));

            return HomeNewsResponse.builder()
                    .success(true)
                    .status(200)
                    .message("요청이 성공적으로 처리되었습니다.")
                    .data(HomeNewsResponse.DataBody.builder()
                            .thumbnailUrl(selected.getThumbnailUrl())
                            .title(selected.getTitle())
                            .publisher(selected.getPublisher())
                            .topic(selected.getTopic())
                            .build())
                    .build();
        } catch (Exception e) {
            return HomeNewsResponse.builder()
                    .success(false)
                    .status(500)
                    .message(e.getMessage())
                    .data(null)
                    .build();
        }
    }

    public HomeCoursesResponse getCourses(Long userId, String type, String view) {
        try {
            List<HomeCourse> allCourses = homeCourseRepository.findByUserIdAndTypeOrderByIdDesc(userId, type);
            if (allCourses.isEmpty()) {
                return HomeCoursesResponse.builder()
                        .success(true)
                        .status(200)
                        .message("조회된 코스가 없습니다.")
                        .data(List.of())
                        .build();
            }

            int limit = "preview".equalsIgnoreCase(view) ? 6 : allCourses.size();
            List<HomeCoursesResponse.DataBody> data = allCourses.stream()
                    .limit(limit)
                    .map(c -> HomeCoursesResponse.DataBody.builder()
                            .thumbnailUrl(c.getThumbnailUrl())
                            .title(c.getTitle())
                            .longDescription(c.getLongDescription())
                            .topic(c.getTopic())
                            .build())
                    .toList();

            return HomeCoursesResponse.builder()
                    .success(true)
                    .status(200)
                    .message("요청이 성공적으로 처리되었습니다.")
                    .data(data)
                    .build();
        } catch (Exception e) {
            return HomeCoursesResponse.builder()
                    .success(false)
                    .status(500)
                    .message(e.getMessage())
                    .data(null)
                    .build();
        }
    }

    public AttendanceStreakResponse getAttendanceStreak(Long userId) {
        try {
            int streak = 5;
            return AttendanceStreakResponse.builder()
                    .success(true)
                    .status(200)
                    .message("요청이 성공적으로 처리되었습니다.")
                    .data(new AttendanceStreakResponse.DataBody(streak))
                    .build();
        } catch (Exception e) {
            return AttendanceStreakResponse.builder()
                    .success(false)
                    .status(500)
                    .message(e.getMessage())
                    .data(null)
                    .build();
        }
    }
}

