package com.niedu.dto.my;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "용어 사전 전체 용어 목록 응답 DTO")
public record MyTermListResponse(
        @Schema(description = "용어 그룹 목록")
        List<TermGroupRecord> groups
) {}