package io.dodn.commerce.core.api.controller.v1.request;

public record ApplyFavoriteRequest(
        Long productId,
        ApplyFavoriteRequestType type
) {
}
