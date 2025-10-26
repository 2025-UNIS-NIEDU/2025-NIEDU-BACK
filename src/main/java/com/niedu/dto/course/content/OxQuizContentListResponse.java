package com.niedu.dto.course.content;

import java.util.List;

public record OxQuizContentListResponse(
        String sourceUrl,
        List<OxQuizContentResponse> contents
) implements ContentResponse{
}
