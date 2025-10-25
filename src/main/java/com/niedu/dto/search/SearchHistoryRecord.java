package com.niedu.dto.search;

import com.niedu.entity.search.SearchLog;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Schema(description = "검색 기록 응답 Record")
public record SearchHistoryRecord(
        @Schema(description = "검색 기록 ID")
        Long logId,
        @Schema(description = "검색 키워드")
        String keyword,
        @Schema(description = "검색 시간")
        LocalDateTime searchedAt
) {

    public static SearchHistoryRecord fromEntity(SearchLog searchLog) {
        return new SearchHistoryRecord(
                searchLog.getId(),
                searchLog.getKeyword(),
                searchLog.getSearchedAt()
        );
    }

    public static List<SearchHistoryRecord> fromEntities(List<SearchLog> searchLogs) {
        return searchLogs.stream()
                .map(SearchHistoryRecord::fromEntity)
                .collect(Collectors.toList());
    }
}