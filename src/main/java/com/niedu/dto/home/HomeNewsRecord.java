package com.niedu.dto.home;

import com.niedu.entity.content.NewsRef;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "홈 화면 뉴스 데이터 Record")
public record HomeNewsRecord(
        @Schema(description = "썸네일 URL")
        String thumbnailUrl,
        @Schema(description = "뉴스 제목")
        String title,
        @Schema(description = "언론사")
        String publisher,
        @Schema(description = "토픽")
        String topic
) {
    public static HomeNewsRecord fromEntity(NewsRef newsRef) {
        return new HomeNewsRecord(
                newsRef.getThumbnailUrl(),
                newsRef.getHeadline(),
                newsRef.getPublisher(),
                newsRef.getTopic()
        );
    }
}