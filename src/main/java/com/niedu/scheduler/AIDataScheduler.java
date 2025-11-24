package com.niedu.scheduler;

import com.niedu.service.edu.AIService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AIDataScheduler {
    private final AIService aiService;

    // 매일 오후 9시 35분 (21:35:00)에 실행되도록 수정
    @Scheduled(cron = "0 54 21 * * *", zone = "Asia/Seoul")
    public void syncAIData() {
        log.info("AI 서버 Data 동기화 시작");
        aiService.syncAIData();
        log.info("AI 서버 Data 동기화 완료");
    }
}