package io.dodn.commerce.core.api.controller.v1.request;

import io.dodn.commerce.core.enums.FavoriteTargetType;
import io.dodn.commerce.core.support.error.CoreException;
import io.dodn.commerce.core.support.error.ErrorType;

public record ApplyFavoriteRequest(
        Long productId,
        FavoriteTargetType targetType,
        Long targetId,
        ApplyFavoriteRequestType type
) {

    public FavoriteTargetType resolveTargetType() {
        return targetType != null ? targetType : FavoriteTargetType.PRODUCT;
    }

    public Long resolveTargetId() {
        Long resolvedId = targetId != null ? targetId : productId;
        if (resolvedId == null) {
            throw new CoreException(ErrorType.INVALID_REQUEST);
        }
        return resolvedId;
    }


    public enum ApplyFavoriteRequestType {
        FAVORITE,
        UNFAVORITE,
    }
}
