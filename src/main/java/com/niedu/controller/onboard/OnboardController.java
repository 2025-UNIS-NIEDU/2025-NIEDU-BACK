package com.niedu.controller.onboard;

import com.niedu.entity.topic.Topic;
import com.niedu.entity.topic.UserTopicPreference;
import com.niedu.entity.user.User;
import com.niedu.global.response.ApiResponse;
import com.niedu.repository.topic.TopicRepository;
import com.niedu.repository.topic.UserTopicPreferenceRepository;
import com.niedu.service.auth.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Objects;

@RestController
@RequestMapping("/api/onboard")
@RequiredArgsConstructor
@Tag(name = "온보딩", description = "온보딩 관련 API")
@SecurityRequirement(name = "accessToken")
public class OnboardController {
    private final AuthService authService;
    private final UserTopicPreferenceRepository userTopicPreferenceRepository;
    private final TopicRepository topicRepository;

    @Operation(
            summary = "선호 토픽 선택",
            description = "FUNCTION ID: ONB-TOPIC-01~ONB-TOPIC-03"
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "선호 토픽 목록",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(value = "[\"경제\",\"정치\"]")
            )
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "OK",
                    content = @Content(schema = @Schema(
                            implementation = com.niedu.global.response.ApiResponse.class,
                            description = "data: String"
                    ))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Bad Request",
                    content = @Content(schema = @Schema(implementation = com.niedu.global.response.ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = com.niedu.global.response.ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Forbidden",
                    content = @Content(schema = @Schema(implementation = com.niedu.global.response.ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = com.niedu.global.response.ApiResponse.class))
            )
    })
    @PostMapping("/topics")
    public ResponseEntity<ApiResponse<?>> saveMyFavTopics(HttpServletRequest httpServletRequest,
                                                          @RequestBody ArrayList<String> topics){
        User user = authService.getUserFromRequest(httpServletRequest);
        topics.stream()
                .map(topicRepository::findByName)
                .filter(Objects::nonNull)
                .map(topic -> new UserTopicPreference(user, topic))
                .forEach(userTopicPreferenceRepository::save);
        return ResponseEntity.ok(ApiResponse.success("선호 토픽이 저장되었습니다."));
    }
}
