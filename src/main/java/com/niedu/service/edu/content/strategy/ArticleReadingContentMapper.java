package com.niedu.service.edu.content.strategy;

import com.niedu.dto.course.content.ArticleReadingContentResponse;
import com.niedu.dto.course.content.ContentResponse;
import com.niedu.entity.content.Content;
import com.niedu.entity.content.NewsRef;
import com.niedu.entity.course.Step;
import com.niedu.entity.course.StepType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ArticleReadingContentMapper implements ContentMapperStrategy {
    @Override
    public boolean supports(StepType type) {
        if (!type.equals(StepType.ARTICLE_READING)) return false;
        return true;
    }

    @Override
    public ContentResponse toResponse(Step step, List<Content> contents) {
        NewsRef newsRef = step.getSession().getNewsRef();
        return new ArticleReadingContentResponse(
                newsRef.getThumbnailUrl(),
                newsRef.getHeadline(),
                newsRef.getPublisher(),
                newsRef.getPublishedAt(),
                newsRef.getSourceUrl()
        );
    }
}
