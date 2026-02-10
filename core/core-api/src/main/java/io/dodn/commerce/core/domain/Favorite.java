package io.dodn.commerce.core.domain;

import java.time.LocalDateTime;

public record Favorite(
        Long id,
        Long userId,
        FavoriteTarget target,
        LocalDateTime favoritedAt
) {
}
