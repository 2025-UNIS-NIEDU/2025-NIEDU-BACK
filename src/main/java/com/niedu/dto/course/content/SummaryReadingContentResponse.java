package com.niedu.dto.course.content;

import java.util.List;

public record SummaryReadingContentResponse (
        String summary,
        List<KeywordContent> keywords
) implements ContentResponse {}