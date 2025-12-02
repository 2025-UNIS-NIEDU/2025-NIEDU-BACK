package com.niedu.dto.course;

import java.time.LocalDate;

public record SessionListResponse(
        Long id,
        String thumbnailUrl,
        String headline,
        String publisher,
        LocalDate publishedAt
) {}