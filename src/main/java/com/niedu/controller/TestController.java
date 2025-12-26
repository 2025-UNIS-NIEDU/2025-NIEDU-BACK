package com.niedu.controller;

import com.niedu.service.edu.AIService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestController {
    private final AIService aiService;

    @GetMapping("/api/test")
    public ResponseEntity<?> test() {
        aiService.syncAllAICourses();
        return ResponseEntity.ok().build();
    }
}
