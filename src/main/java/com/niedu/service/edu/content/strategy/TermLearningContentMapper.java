package com.niedu.service.edu.content.strategy;

import com.niedu.dto.course.content.ContentResponse;
import com.niedu.dto.course.content.TermContent;
import com.niedu.dto.course.content.TermLearningContentResponse;
import com.niedu.entity.content.Content;
import com.niedu.entity.content.Term;
import com.niedu.entity.course.Step;
import com.niedu.entity.course.StepType;
import com.niedu.repository.content.TermRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TermLearningContentMapper implements ContentMapperStrategy {
    private final TermRepository termRepository;

    @Override
    public boolean supports(StepType type) {
        if (!type.equals(StepType.TERM_LEARNING)) return false;
        return true;
    }

    @Override
    public ContentResponse toResponse(Step step) {
        List<Term> terms = termRepository.findAllBySession(step.getSession());
        ArrayList<TermContent> termContents = terms.stream()
                .map(term -> new TermContent(
                        term.getId(),
                        term.getName(),
                        term.getDefinition(),
                        term.getExampleSentence(),
                        term.getAdditionalExplanation()
                ))
                .collect(Collectors.toCollection(ArrayList::new));
        return new TermLearningContentResponse(termContents);
    }
}
