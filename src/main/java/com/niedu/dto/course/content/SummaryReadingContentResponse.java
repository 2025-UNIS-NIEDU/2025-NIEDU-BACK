package com.niedu.dto.course.content;

import java.util.ArrayList;

public record SummaryReadingContentResponse (
        String summary,
        ArrayList<KeywordContent> keywords
) implements ContentResponse {}