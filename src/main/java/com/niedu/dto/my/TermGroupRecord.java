package com.niedu.dto.my; // 패키지명 확인

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "용어 사전 그룹")
@JsonInclude(JsonInclude.Include.NON_NULL) // Null이 아닌 필드만 JSON에 포함
public record TermGroupRecord(
        @Schema(description = "초성 (가나다순 정렬 시)")
        String initial,

        @Schema(description = "기간 (최근저장순 정렬 시)")
        String period,

        @Schema(description = "용어 목록")
        List<TermItemRecord> terms
) {

    public static TermGroupRecord forAlphabetical(String initial, List<TermItemRecord> terms) {
        return new TermGroupRecord(initial, null, terms);
    }

    public static TermGroupRecord forRecent(String period, List<TermItemRecord> terms) {
        return new TermGroupRecord(null, period, terms);
    }
}