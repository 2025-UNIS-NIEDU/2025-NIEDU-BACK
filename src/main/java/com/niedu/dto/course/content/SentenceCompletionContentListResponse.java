package com.niedu.dto.course.content;

import java.util.List;

public record SentenceCompletionContentListResponse(
        List<SentenceCompletionContentResponse> contents
) implements ContentResponse{
}
