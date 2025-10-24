package com.niedu.dto.favorite;

public record FavoriteRequest(
        String type,
        Long targetId
) {
}