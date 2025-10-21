package com.niedu.dto.course;

import java.time.LocalDateTime;

public record SessionResponse (
        String thumbnailUrl,
        String headline,
        String publisher,
        LocalDateTime publishedAt
) {}