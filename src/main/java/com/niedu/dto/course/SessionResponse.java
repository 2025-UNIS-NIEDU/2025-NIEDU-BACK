package com.niedu.dto.course;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class SessionResponse {
    private String thumbnailUrl;
    private String headline;
    private String publisher;
    private LocalDateTime publishedAt;
}
