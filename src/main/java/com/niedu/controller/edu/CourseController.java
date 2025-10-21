package com.niedu.controller.edu;

import com.niedu.dto.course.CourseListResponse;
import com.niedu.dto.course.CourseResponse;
import com.niedu.entity.user.User;
import com.niedu.global.response.ApiResponse;
import com.niedu.service.auth.AuthService;
import com.niedu.service.edu.CourseService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/api/edu/courses")
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;
    private final AuthService authService;

    @GetMapping("")
    public ResponseEntity<ApiResponse<?>> getCourses(HttpServletRequest httpServletRequest,
                                                     @RequestParam("view") String view,
                                                     @RequestParam("topic") String topic) {
        User user = authService.getUserFromRequest(httpServletRequest);
        ArrayList<CourseListResponse> responses = courseService.getCourses(user, view, topic);
        return (responses != null)?
                ResponseEntity.ok(ApiResponse.success(responses)):
                ResponseEntity.internalServerError().body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "코스 목록 조회에 실패했습니다."));
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<ApiResponse<?>> getCourse(@PathVariable("courseId") Long courseId) {
        CourseResponse response = courseService.getCourse(courseId);
        return (response != null)?
                ResponseEntity.ok(ApiResponse.success(response)):
                ResponseEntity.internalServerError().body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "코스 조회에 실패했습니다."));
    }
}
