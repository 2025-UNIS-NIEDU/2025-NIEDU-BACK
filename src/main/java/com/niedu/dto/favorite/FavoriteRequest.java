package com.niedu.dto.favorite;

import com.niedu.global.enums.FavoriteType;

public record FavoriteRequest(
        FavoriteType type,
        Long targetId
) {
}