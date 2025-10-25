package com.niedu.dto.search;


import com.niedu.entity.course.Course;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Schema(description = "코스 검색 결과 응답 Record")
public record CourseSearchResultRecord(
        @Schema(description = "썸네일 URL")
        String thumbnailUrl,
        @Schema(description = "코스 제목")
        String title,
        @Schema(description = "관련 태그 목록 (예: SubTopic 이름들)")
        List<String> tags,
        @Schema(description = "코스 짧은 설명")
        String shortDescription
) {
    public static CourseSearchResultRecord fromEntity(Course course) {
        List<String> fetchedTags = Collections.emptyList();
        return new CourseSearchResultRecord(
                course.getThumbnailUrl(),
                course.getTitle(),
                fetchedTags,
                course.getShortDescription()
        );
    }

    public static List<CourseSearchResultRecord> fromEntities(List<Course> courses) {
        return courses.stream()
                .map(CourseSearchResultRecord::fromEntity)
                .collect(Collectors.toList());
    }
}