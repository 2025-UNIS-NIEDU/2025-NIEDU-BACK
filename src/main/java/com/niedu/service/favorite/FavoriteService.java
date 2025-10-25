package com.niedu.service.favorite;

import com.niedu.dto.favorite.FavoriteRequest;
import com.niedu.dto.favorite.IsFavoriteResponse;
import com.niedu.entity.content.Term;
import com.niedu.entity.course.Course;
import com.niedu.entity.learning_record.SavedCourse;
import com.niedu.entity.learning_record.SavedCourseId;
import com.niedu.entity.learning_record.SavedTerm;
import com.niedu.entity.user.User;
import com.niedu.global.enums.FavoriteType;
import com.niedu.repository.content.TermRepository;
import com.niedu.repository.course.CourseRepository;
import com.niedu.repository.learning_record.SavedCourseRepository;
import com.niedu.repository.learning_record.SavedTermRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FavoriteService {

    private final CourseRepository courseRepository;
    private final TermRepository termRepository;
    private final SavedCourseRepository savedCourseRepository;
    private final SavedTermRepository savedTermRepository;

    @Transactional
    public void addFavorite(User user, FavoriteRequest request) {
        switch (request.type()) {
            case COURSE -> addCourseFavorite(user, request.targetId());
            case TERM -> addTermFavorite(user, request.targetId());
        }
    }

    @Transactional
    public void deleteFavorite(User user, FavoriteRequest request) {
        switch (request.type()) {
            case COURSE -> deleteCourseFavorite(user, request.targetId());
            case TERM -> deleteTermFavorite(user, request.targetId());
        }
    }

    public IsFavoriteResponse checkIsFavorite(User user, FavoriteType type, Long targetId) {
        boolean exists = switch (type) {
            case COURSE -> savedCourseRepository.existsById(new SavedCourseId(user.getId(), targetId));
            case TERM -> {
                Term term = termRepository.findById(targetId).orElse(null);
                yield term != null && savedTermRepository.existsByUserAndTerm(user, term);
            }
        };
        return new IsFavoriteResponse(exists);
    }

    private void addCourseFavorite(User user, Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 코스입니다."));

        SavedCourseId id = new SavedCourseId(user.getId(), courseId);
        if (savedCourseRepository.existsById(id)) {
            throw new IllegalStateException("이미 즐겨찾기에 추가된 코스입니다.");
        }
        savedCourseRepository.save(new SavedCourse(user, course));
    }

    private void addTermFavorite(User user, Long termId) {
        Term term = termRepository.findById(termId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 용어입니다."));

        if (savedTermRepository.existsByUserAndTerm(user, term)) {
            throw new IllegalStateException("이미 즐겨찾기에 추가된 용어입니다.");
        }
        savedTermRepository.save(new SavedTerm(user, term));
    }

    private void deleteCourseFavorite(User user, Long courseId) {
        SavedCourseId id = new SavedCourseId(user.getId(), courseId);
        if (!savedCourseRepository.existsById(id)) return;
        savedCourseRepository.deleteById(id);
    }

    private void deleteTermFavorite(User user, Long termId) {
        Term term = termRepository.findById(termId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 용어입니다."));
        if (savedTermRepository.existsByUserAndTerm(user, term)) {
            savedTermRepository.deleteByUserAndTerm(user, term);
        }
    }
}
