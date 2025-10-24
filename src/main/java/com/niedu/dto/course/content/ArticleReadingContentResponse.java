package com.niedu.dto.course.content;

import java.time.LocalDate;

public record ArticleReadingContentResponse (
        String thumbnailUrl,
        String headline,
        String publisher,
        LocalDate publishedAt,
        String sourceUrl
) implements ContentResponse {}