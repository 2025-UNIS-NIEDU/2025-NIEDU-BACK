package com.niedu.dto.search;

import com.niedu.entity.search.SearchLog;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Schema(description = "검색 기록 응답 Record")
public record SearchHistoryResponse(
        @Schema(description = "검색 기록 ID")
        Long logId,
        @Schema(description = "검색 키워드")
        String keyword,
        @Schema(description = "검색 시간")
        LocalDateTime searchedAt
) {

    public static SearchHistoryResponse fromEntity(SearchLog searchLog) {
        return new SearchHistoryResponse(
                searchLog.getId(),
                searchLog.getKeyword(),
                searchLog.getSearchedAt()
        );
    }

    public static List<SearchHistoryResponse> fromEntities(List<SearchLog> searchLogs) {
        return searchLogs.stream()
                .map(SearchHistoryResponse::fromEntity)
                .collect(Collectors.toList());
    }
}