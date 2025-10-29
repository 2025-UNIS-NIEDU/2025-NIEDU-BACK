package com.niedu.service.edu.user_answer.strategy;

import com.niedu.dto.course.is_correct.IsCorrectResponse;
import com.niedu.dto.course.user_answer.AnswerResponse;
import com.niedu.entity.content.Content;
import com.niedu.entity.course.StepType;
import com.niedu.entity.learning_record.StudiedStep;
import com.niedu.entity.learning_record.user_answer.UserAnswer;

import java.util.List;
import java.util.Map;

public interface UserAnswerMapperStrategy {
    boolean supports(StepType type);

    AnswerResponse toResponse(List<UserAnswer> userAnswers);

    List<UserAnswer> toEntities(StudiedStep studiedStep, List<Content> contents, AnswerResponse request);

    void updateEntities(List<UserAnswer> existingUserAnswers, List<Content> contents, AnswerResponse request);

    List<IsCorrectResponse> checkIsCorrect(List<Content> contents, AnswerResponse request);
}