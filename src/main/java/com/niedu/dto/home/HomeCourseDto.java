package com.niedu.dto.home;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public class HomeCourseDto {

    @Schema(description = "코스 정보 항목 DTO")
    public record CourseInfo(
            @Schema(description = "썸네일 URL")
            String thumbnailUrl,

            @Schema(description = "코스 제목")
            String title,

            @Schema(description = "코스 상세 설명")
            String longDescription,

            @Schema(description = "토픽")
            String topic
    ) {}

    @Schema(description = "홈 화면 코스 목록 응답")
    public record CourseListResponse(
            List<CourseInfo> courses
    ) {}
}