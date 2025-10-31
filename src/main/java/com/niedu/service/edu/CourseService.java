package com.niedu.service.edu;

import com.niedu.dto.course.CourseListResponse;
import com.niedu.dto.course.CourseResponse;
import com.niedu.entity.course.Course;
import com.niedu.entity.user.User;
import com.niedu.global.enums.CourseType;
import com.niedu.global.enums.CourseView;
import com.niedu.repository.course.CourseRepository;
import com.niedu.repository.course.CourseSubTopicRepository;
import com.niedu.repository.learning_record.StudiedCourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {
    private static CourseRepository courseRepository;
    private static CourseSubTopicRepository courseSubTopicRepository;
    private static StudiedCourseRepository studiedCourseRepository;

    public ArrayList<CourseListResponse> getCourses(User user, String type, String view, String topic, Integer page) {
        CourseType courseType = parseEnum(type, CourseType.class, CourseType.RECENT);
        CourseView courseView = parseEnum(view, CourseView.class, CourseView.PREVIEW);

        ArrayList<CourseListResponse> responses = new ArrayList<>();

        switch (courseType) {
            case RECENT -> responses = findRecentCourses(courseView, topic, page);
            case POPULAR -> responses = findPopularCourses(courseView, page);
            case CUSTOM -> responses = findCustomCourses(user, courseView, page);
            case NEW -> responses = findNewCourses(user, courseView, page);
        }
        return responses;
    }

    public CourseResponse getCourse(User user, Long courseId) {
        Course course = courseRepository.findById(courseId).orElse(null);
        if (course == null) return null;
        return new CourseResponse(
                course.getThumbnailUrl(),
                course.getTitle(),
                course.getTopic().getName(),
                studiedCourseRepository.findByUser_IdAndCourse_Id(user.getId(), course.getId()).getProgress(),
                course.getDescription()
        );
    }

    // 구현체
    private ArrayList<CourseListResponse> findRecentCourses(CourseView courseView, String topic, Integer page) {
        switch (courseView) {
            case PREVIEW -> {
                List<Course> courses = courseRepository.findByTopic_NameIgnoreCaseOrderByCreatedAtDesc(topic, PageRequest.of(0, 9));
                ArrayList<CourseListResponse> responses = courses.stream()
                        .map(course -> new CourseListResponse(
                                course.getTitle(),
                                course.getThumbnailUrl(),
                                null,
                                null,
                                null
                        ))
                        .collect(Collectors.toCollection(ArrayList::new));
                return responses;
            }
            case ALL -> {
                List<Course> courses = courseRepository.findByTopic_NameIgnoreCaseOrderByCreatedAtDesc(topic, PageRequest.of(page, 10));
                ArrayList<CourseListResponse> responses = courses.stream()
                        .map(course -> new CourseListResponse(
                                course.getTitle(),
                                course.getThumbnailUrl(),
                                course.getDescription(),
                                course.getTopic().getName(),
                                courseSubTopicRepository.findFirstByCourse_Id(course.getId()).getSubTopic().getName()
                        ))
                        .collect(Collectors.toCollection(ArrayList::new));
                return responses;
            }
            case null, default -> {
                return new ArrayList<>();
            }
        }
    }

    private ArrayList<CourseListResponse> findPopularCourses(CourseView courseView, Integer page) {
        switch (courseView) {
            case PREVIEW -> {
                List<Course> courses = courseRepository.findAllByOrderByViewCountDesc(PageRequest.of(0, 9));
                ArrayList<CourseListResponse> responses = courses.stream()
                        .map(course -> new CourseListResponse(
                                course.getTitle(),
                                course.getThumbnailUrl(),
                                null,
                                null,
                                null
                        ))
                        .collect(Collectors.toCollection(ArrayList::new));
                return responses;
            }
            case ALL -> {
                List<Course> courses = courseRepository.findAllByOrderByViewCountDesc(PageRequest.of(page, 10));
                ArrayList<CourseListResponse> responses = courses.stream()
                        .map(course -> new CourseListResponse(
                                course.getTitle(),
                                course.getThumbnailUrl(),
                                course.getDescription(),
                                course.getTopic().getName(),
                                courseSubTopicRepository.findFirstByCourse_Id(course.getId()).getSubTopic().getName()
                        ))
                        .collect(Collectors.toCollection(ArrayList::new));
                return responses;
            }
            case null, default -> {
                return new ArrayList<>();
            }
        }
    }

    private ArrayList<CourseListResponse> findCustomCourses(User user, CourseView courseView, Integer page) {
        switch (courseView) {
            case PREVIEW -> {
                List<Course> courses = courseRepository.findCoursesByUserPreferredTopicsOrderByViewCountDesc(user.getId(), PageRequest.of(0, 9));
                ArrayList<CourseListResponse> responses = courses.stream()
                        .map(course -> new CourseListResponse(
                                course.getTitle(),
                                course.getThumbnailUrl(),
                                null,
                                null,
                                null
                        ))
                        .collect(Collectors.toCollection(ArrayList::new));
                return responses;
            }
            case ALL -> {
                List<Course> courses = courseRepository.findCoursesByUserPreferredTopicsOrderByViewCountDesc(user.getId(), PageRequest.of(page, 10));
                ArrayList<CourseListResponse> responses = courses.stream()
                        .map(course -> new CourseListResponse(
                                course.getTitle(),
                                course.getThumbnailUrl(),
                                course.getDescription(),
                                course.getTopic().getName(),
                                courseSubTopicRepository.findFirstByCourse_Id(course.getId()).getSubTopic().getName()
                        ))
                        .collect(Collectors.toCollection(ArrayList::new));
                return responses;
            }
            case null, default -> {
                return new ArrayList<>();
            }
        }
    }

    private ArrayList<CourseListResponse> findNewCourses(User user, CourseView courseView, Integer page) {
        switch (courseView) {
            case PREVIEW -> {
                List<Course> courses = courseRepository.findCoursesByTopicsUserDidNotPreferOrderByViewCountDesc(user.getId(), PageRequest.of(0, 9));
                if (courses.isEmpty()) courses = courseRepository.findRandomUnstudiedCoursesByUser(user.getId(), PageRequest.of(0, 9));
                ArrayList<CourseListResponse> responses = courses.stream()
                        .map(course -> new CourseListResponse(
                                course.getTitle(),
                                course.getThumbnailUrl(),
                                null,
                                null,
                                null
                        ))
                        .collect(Collectors.toCollection(ArrayList::new));
                return responses;
            }
            case ALL -> {
                List<Course> courses = courseRepository.findCoursesByTopicsUserDidNotPreferOrderByViewCountDesc(user.getId(), PageRequest.of(page, 9));
                if (courses.isEmpty()) courses = courseRepository.findRandomUnstudiedCoursesByUser(user.getId(), PageRequest.of(page, 9));
                ArrayList<CourseListResponse> responses = courses.stream()
                        .map(course -> new CourseListResponse(
                                course.getTitle(),
                                course.getThumbnailUrl(),
                                course.getDescription(),
                                course.getTopic().getName(),
                                courseSubTopicRepository.findFirstByCourse_Id(course.getId()).getSubTopic().getName()
                        ))
                        .collect(Collectors.toCollection(ArrayList::new));
                return responses;
            }
            case null, default -> {
                return new ArrayList<>();
            }
        }
    }


    /// 유틸
    private static <E extends Enum<E>> E parseEnum(String value, Class<E> enumClass, E defaultValue) {
        if (value == null || value.isBlank()) return defaultValue;
        for (E e : enumClass.getEnumConstants()) {
            if (e.name().equalsIgnoreCase(value)) return e;
        }
        return defaultValue; // 지정 외 입력이면 기본값
    }
}