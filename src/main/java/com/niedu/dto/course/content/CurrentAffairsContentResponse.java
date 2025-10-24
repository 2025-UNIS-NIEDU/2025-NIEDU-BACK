package com.niedu.dto.course.content;

public record CurrentAffairsContentResponse (
        String issue,
        String cause,
        String circumstance,
        String result,
        String effect
) implements ContentResponse {}