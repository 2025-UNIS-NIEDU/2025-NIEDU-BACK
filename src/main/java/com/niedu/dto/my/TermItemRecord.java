package com.niedu.dto.my;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "용어 사전에 저장된 용어 항목")
public record TermItemRecord(
        @Schema(description = "용어 ID")
        Long termId,
        @Schema(description = "용어명")
        String term
) {}