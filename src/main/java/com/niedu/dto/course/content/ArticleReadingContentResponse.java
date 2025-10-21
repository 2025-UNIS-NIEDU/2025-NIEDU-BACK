package com.niedu.dto.course.content;

import java.time.LocalDateTime;

public record ArticleReadingContentResponse (
        String thumbnailUrl,
        String headline,
        String publisher,
        LocalDateTime publishedAt,
        String sourceUrl
) implements ContentResponse {}