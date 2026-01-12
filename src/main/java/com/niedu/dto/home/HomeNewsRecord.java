package com.niedu.dto.home;

import com.niedu.entity.course.Session;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "홈 화면 뉴스 데이터 Record")
public record HomeNewsRecord(
        @Schema(description= "Course ID")
        Long id,
        @Schema(description = "썸네일 URL")
        String thumbnailUrl,
        @Schema(description = "뉴스 제목")
        String title,
        @Schema(description = "언론사")
        String publisher,
        @Schema(description = "토픽")
        String topic
) {
    public static HomeNewsRecord fromEntity(Session session) {
        return new HomeNewsRecord(
                session.getCourse().getId(),
                session.getNewsRef().getThumbnailUrl(),
                session.getNewsRef().getHeadline(),
                session.getNewsRef().getPublisher(),
                session.getNewsRef().getTopic()
        );
    }
}
