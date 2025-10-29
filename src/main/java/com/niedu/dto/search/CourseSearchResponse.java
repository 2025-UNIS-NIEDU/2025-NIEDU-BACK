package com.niedu.dto.search;


import com.niedu.entity.course.Course;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Schema(description = "코스 검색 결과 응답 Record")
public record CourseSearchResponse(
        @Schema(description = "썸네일 URL")
        String thumbnailUrl,
        @Schema(description = "코스 제목")
        String title,
        @Schema(description = "관련 태그 목록 (예: SubTopic 이름들)")
        List<String> tags,
        @Schema(description = "코스 짧은 설명")
        String shortDescription
) {
    public static CourseSearchResponse fromEntity(Course course) {
        List<String> fetchedTags;
        if (course.getTopic() != null && course.getTopic().getName() != null) {
            fetchedTags = List.of(course.getTopic().getName());
        } else {
            fetchedTags = Collections.emptyList();
        }

        return new CourseSearchResponse(
                course.getThumbnailUrl(),
                course.getTitle(),
                fetchedTags,
                course.getShortDescription()
        );
    }

    public static List<CourseSearchResponse> fromEntities(List<Course> courses) {
        return courses.stream()
                .map(CourseSearchResponse::fromEntity)
                .collect(Collectors.toList());
    }
}