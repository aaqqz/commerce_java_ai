package io.dodn.commerce.core.domain;

import io.dodn.commerce.core.enums.FavoriteTargetType;

import java.time.LocalDateTime;

public record Favorite(
        Long id,
        Long userId,
        FavoriteTargetType targetType,
        Long targetId,
        LocalDateTime favoritedAt
) {
}
