package com.niedu.dto.course.content;

import java.util.List;

public record MultipleChoiceContentListResponse(
        String sourceUrl,
        List<MultipleChoiceContentResponse> contents
) implements ContentResponse {}
