package com.niedu.service.search;

import com.niedu.dto.search.CourseSearchResultRecord;
import com.niedu.dto.search.SearchHistoryRecord;
import com.niedu.entity.course.Course;
import com.niedu.entity.search.SearchLog;
import com.niedu.entity.user.User;
import com.niedu.repository.course.CourseRepository;
import com.niedu.repository.search.SearchLogRepository;
import com.niedu.repository.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchService {

    private final SearchLogRepository searchLogRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    public List<SearchHistoryRecord> getSearchHistory(Long userId) {
        Pageable pageable = PageRequest.of(0, 10);
        List<SearchLog> logs = searchLogRepository.findByUser_IdOrderBySearchedAtDesc(userId, pageable);
        return SearchHistoryRecord.fromEntities(logs);
    }

    @Transactional
    public void deleteSearchHistory(Long userId, Long logId) {
        searchLogRepository.deleteByLogIdAndUser_Id(logId, userId);
    }

    public List<String> getSearchSuggestions(Long userId) {
        Pageable topFive = PageRequest.of(0, 5);
        return searchLogRepository.findPopularKeywords(topFive);
    }

    @Transactional
    public List<CourseSearchResultRecord> searchCourses(Long userId, String keyword, String sort) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        SearchLog searchLog = new SearchLog(user, keyword);
        searchLogRepository.save(searchLog);

        Pageable pageable = PageRequest.of(0, 10);
        List<Course> courses;

        if ("recent".equalsIgnoreCase(sort)) {
            courses = courseRepository.findByTitleContainingIgnoreCaseOrderByCreatedAtDesc(keyword, pageable);
        } else if ("popular".equalsIgnoreCase(sort)) {
            courses = courseRepository.findByTitleContainingIgnoreCaseOrderByViewCountDesc(keyword, pageable);
        } else {
            throw new IllegalArgumentException("Invalid sort parameter: " + sort);
        }

        return CourseSearchResultRecord.fromEntities(courses);
    }
}