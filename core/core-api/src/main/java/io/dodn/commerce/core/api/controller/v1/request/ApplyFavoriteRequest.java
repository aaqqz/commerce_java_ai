package io.dodn.commerce.core.api.controller.v1.request;

import io.dodn.commerce.core.enums.FavoriteTargetType;

public record ApplyFavoriteRequest(
        FavoriteTargetType targetType,
        Long targetId,
        ApplyFavoriteRequestType type
) {
    public FavoriteTargetType getTargetType() {
        return targetType != null ? targetType : FavoriteTargetType.PRODUCT;
    }
}
