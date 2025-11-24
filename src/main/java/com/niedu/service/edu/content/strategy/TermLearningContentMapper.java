package com.niedu.service.edu.content.strategy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.niedu.dto.course.ai.AIStepResponse;
import com.niedu.dto.course.content.ContentResponse;
import com.niedu.dto.course.content.TermContent;
import com.niedu.dto.course.content.TermLearningContentResponse;
import com.niedu.entity.content.Content;
import com.niedu.entity.content.Term;
import com.niedu.entity.course.Step;
import com.niedu.entity.course.StepType;
import com.niedu.repository.content.TermRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
@RequiredArgsConstructor
public class TermLearningContentMapper implements ContentMapperStrategy {
    private final ObjectMapper objectMapper;
    private final TermRepository termRepository;

    @Override
    public boolean supports(StepType type) {
        if (!type.equals(StepType.TERM_LEARNING)) return false;
        return true;
    }

    @Override
    public ContentResponse toResponse(Step step, List<Content> contents) {
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

    @Override
    public List<Content> toEntities(Step step, AIStepResponse dto) {
        log.warn("[RAW CONTENTS] {}", dto.contents());
        for (Object obj : dto.contents()) {

            // obj가 Map인지 100% 확인
            if (!(obj instanceof Map<?, ?> item)) continue;

            // terms 꺼내기
            Object rawTerms = item.get("terms");
            if (!(rawTerms instanceof List<?> terms)) continue;

            for (Object t : terms) {

                // termMap인지 확인
                if (!(t instanceof Map<?, ?> termMap)) continue;

                log.warn("[TERM MAP] {}", termMap);
                log.warn(" - name: {} ({})", termMap.get("name"), termMap.get("name").getClass());
                log.warn(" - definition: {} ({})", termMap.get("definition"), termMap.get("definition").getClass());
                log.warn(" - example: {} ({})", termMap.get("exampleSentence"), termMap.get("exampleSentence").getClass());
                log.warn(" - additionalExplanation: {} ({})", termMap.get("additionalExplanation"), termMap.get("additionalExplanation").getClass());

                termRepository.save(
                        new Term(
                                null,
                                toSafeString(termMap.get("name")),
                                step.getSession(),
                                toSafeString(termMap.get("definition")),
                                toSafeString(termMap.get("exampleSentence")),
                                toSafeString(termMap.get("additionalExplanation"))
                        )
                );
            }
        }

        return List.of();
    }

    private String toSafeString(Object value) {
        if (value == null) return null;
        return value.toString();
    }
}