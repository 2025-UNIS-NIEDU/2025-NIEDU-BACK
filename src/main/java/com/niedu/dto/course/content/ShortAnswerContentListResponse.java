package com.niedu.dto.course.content;

import java.util.List;

public record ShortAnswerContentListResponse(
        String sourceUrl,
        List<ShortAnswerContentResponse> contents
) implements ContentResponse { }
