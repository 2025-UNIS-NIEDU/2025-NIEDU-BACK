package com.niedu.dto.my;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "캘린더 내 코스/활동 정보")
@JsonInclude(JsonInclude.Include.NON_NULL)
public record MyCalendarCourseInfo(
        @Schema(description = "토픽명 (한글)")
        String topic,
        @Schema(description = "서브토픽명 (한글)")
        String subTopic,
        @Schema(description = "추가 활동 개수")
        Integer extra
) {

    public static MyCalendarCourseInfo fromTopic(String topic, String subTopic) {
        return new MyCalendarCourseInfo(topic, subTopic, null);
    }

    public static MyCalendarCourseInfo fromExtra(Integer extra) {
        return new MyCalendarCourseInfo(null, null, extra);
    }
}