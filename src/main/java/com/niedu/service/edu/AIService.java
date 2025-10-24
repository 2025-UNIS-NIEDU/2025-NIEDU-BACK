package com.niedu.service.edu;

import com.niedu.dto.course.FeedbackAnswerRequest;
import com.niedu.dto.course.FeedbackAnswerResponse;
import com.niedu.entity.user.User;
import org.springframework.stereotype.Service;

@Service
public class AIService {
    public FeedbackAnswerResponse submitStepAnswerForFeedback(User user, Long stepId, FeedbackAnswerRequest request) {
        return null;
    }
}
