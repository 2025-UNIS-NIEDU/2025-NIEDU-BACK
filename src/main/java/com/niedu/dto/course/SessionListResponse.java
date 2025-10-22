package com.niedu.dto.course;

import java.time.LocalDate;

public record SessionListResponse(
        String thumbnailUrl,
        String headline,
        String publisher,
        LocalDate publishedAt
) {}