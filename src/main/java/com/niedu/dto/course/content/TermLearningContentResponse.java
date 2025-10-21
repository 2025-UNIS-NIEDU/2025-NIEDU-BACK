package com.niedu.dto.course.content;

import java.util.ArrayList;

public record TermLearningContentResponse (
        ArrayList<TermContent> terms
) implements ContentResponse {}