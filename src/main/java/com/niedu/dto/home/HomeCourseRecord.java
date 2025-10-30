package com.niedu.dto.home;

import com.niedu.entity.course.Course;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

public class HomeCourseRecord {
    @Schema(description = "홈 화면 코스 정보 Record")
    public record CourseInfo(
            @Schema(description = "썸네일 URL")
            String thumbnailUrl,
            @Schema(description = "코스 제목")
            String title,
            @Schema(description = "코스 상세 설명")
            String longDescription,
            @Schema(description = "토픽명")
            String topic
    ) {
        public static CourseInfo fromEntity(Course course) {
            return new CourseInfo(
                    course.getThumbnailUrl(),
                    course.getTitle(),
                    course.getDescription(),
                    course.getTopic() != null ? course.getTopic().getName() : null // Topic 엔티티에서 이름 가져오기
            );
        }
    }

    @Schema(description = "홈 화면 코스 목록 응답 Record")
    public record CourseListResponse(
            List<CourseInfo> courses
    ) {
        public static CourseListResponse fromEntities(Page<Course> courses) {
            List<CourseInfo> courseInfos = courses.stream()
                    .map(CourseInfo::fromEntity)
                    .collect(Collectors.toList());
            return new CourseListResponse(courseInfos);
        }
    }
}