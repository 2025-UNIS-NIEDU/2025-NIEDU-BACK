package com.niedu.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.niedu.service.edu.AIService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class TestController {
    private final AIService aiService;

    @GetMapping("/api/test")
    public ResponseEntity<?> test() {
        aiService.syncMockAICourses();
        return ResponseEntity.ok().build();
    }
}
