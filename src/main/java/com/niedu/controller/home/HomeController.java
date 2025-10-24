package com.niedu.controller.home;

import com.niedu.dto.attendance.AttendanceStreakResponse;
import com.niedu.dto.home.*;
import com.niedu.service.home.HomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/home")
@RequiredArgsConstructor
public class HomeController {

    private final HomeService homeService;

    @GetMapping("/news")
    public ResponseEntity<HomeNewsResponse> getRandomNews(
            @CookieValue(name = "accessToken", required = false) String accessToken
    ) {
        if (accessToken == null) {
            return ResponseEntity.status(401).body(
                    HomeNewsResponse.builder()
                            .success(false)
                            .status(401)
                            .message("유효하지 않은 또는 만료된 토큰입니다.")
                            .data(null)
                            .build()
            );
        }

        HomeNewsResponse response = homeService.getRandomNews();
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/courses")
    public ResponseEntity<HomeCoursesResponse> getCourses(
            @CookieValue(name = "accessToken", required = false) String accessToken,
            @RequestParam String type,
            @RequestParam String view
    ) {
        if (accessToken == null) {
            return ResponseEntity.status(401).body(
                    HomeCoursesResponse.builder()
                            .success(false)
                            .status(401)
                            .message("유효하지 않은 또는 만료된 토큰입니다.")
                            .data(null)
                            .build()
            );
        }

        Long userId = extractUserIdFromToken(accessToken);
        HomeCoursesResponse response = homeService.getCourses(userId, type, view);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/attendance/streak")
    public ResponseEntity<AttendanceStreakResponse> getAttendanceStreak(
            @CookieValue(name = "accessToken", required = false) String accessToken
    ) {
        if (accessToken == null) {
            return ResponseEntity.status(401).body(
                    AttendanceStreakResponse.builder()
                            .success(false)
                            .status(401)
                            .message("유효하지 않은 또는 만료된 토큰입니다.")
                            .data(null)
                            .build()
            );
        }

        Long userId = extractUserIdFromToken(accessToken);
        AttendanceStreakResponse response = homeService.getAttendanceStreak(userId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    private Long extractUserIdFromToken(String token) {
        return 1L;
    }
}
