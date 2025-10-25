package com.niedu.dto.course.content;

import java.util.List;

public record OxQuizContentListResponse(
        List<OxQuizContentResponse> contents
) implements ContentResponse{
}
