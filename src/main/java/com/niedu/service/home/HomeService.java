package com.niedu.service.home;

import com.niedu.dto.home.HomeCourseRecord;
import com.niedu.dto.home.HomeNewsRecord;
import com.niedu.entity.content.NewsRef;
import com.niedu.entity.course.Course;
import com.niedu.entity.learning_record.StudiedCourse;
import com.niedu.entity.user.User;
import com.niedu.repository.content.NewsRefRepository;
import com.niedu.repository.course.CourseRepository;
import com.niedu.repository.course.SessionRepository;
import com.niedu.repository.learning_record.SavedCourseRepository;
import com.niedu.repository.learning_record.StudiedCourseRepository;
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

    private final NewsRefRepository newsRefRepository;
    private final CourseRepository courseRepository;
    private final SavedCourseRepository savedCourseRepository;
    private final StudiedCourseRepository studiedCourseRepository;
    private final SessionRepository sessionRepository;

    public List<HomeNewsRecord> getRandomNews(User user) {
        List<NewsRef> newsRefs = newsRefRepository.findTwoRandomFromLatestDate();

        if (newsRefs == null || newsRefs.isEmpty())
            throw new RuntimeException("최신 뉴스가 없습니다.");

        return newsRefs.stream()
                .map(newsRef -> {
                    // 세션을 거쳐 CourseId를 가져옴
                    Long courseId = sessionRepository.findByNewsRef_Id(newsRef.getId())
                            .map(session -> session.getCourse().getId())
                            .orElse(null);

                    return new HomeNewsRecord(
                            courseId,
                            newsRef.getThumbnailUrl(),
                            newsRef.getHeadline(),
                            newsRef.getPublisher()
                    );
                })
                .toList();
    }

    public List<HomeCourseRecord> getCourses(User user, String type, String view) {
        int limit = "preview".equalsIgnoreCase(view) ? 6 : 10;
        Pageable pageable = PageRequest.of(0, limit);

        if ("recent".equalsIgnoreCase(type)) {
            Page<StudiedCourse> studiedCourses = studiedCourseRepository.findByUserOrderByUpdatedAtDesc(user, pageable);

            // Page 객체에서 데이터를 꺼내 리스트로 변환
            return studiedCourses.getContent().stream()
                    .map(sc -> HomeCourseRecord.fromEntity(sc.getCourse()))
                    .toList();
        } else if ("saved".equalsIgnoreCase(type)) {
            Page<Course> courses = savedCourseRepository.findByUserOrderByCourse_CreatedAtDesc(user, pageable);

            return courses.getContent().stream()
                    .map(HomeCourseRecord::fromEntity)
                    .toList();
        } else {
            throw new IllegalArgumentException("타입 파라미터 오류: " + type);
        }
    }
}