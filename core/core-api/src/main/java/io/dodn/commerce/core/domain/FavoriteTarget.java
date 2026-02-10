package io.dodn.commerce.core.domain;

import io.dodn.commerce.core.enums.FavoriteTargetType;

public record FavoriteTarget(
        FavoriteTargetType type,
        Long id
) {
}
