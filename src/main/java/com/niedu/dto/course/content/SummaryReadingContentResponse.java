package com.niedu.dto.course.content;

import java.util.ArrayList;

public record SummaryReadingContentResponse (
        String summary,
        ArrayList<SummaryReadingContentResponse> keywords
) implements ContentResponse {}