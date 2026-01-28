package io.dodn.commerce.core.domain;

public record NewOrderItem(
        Long productId,
        Long quantity
) {
}
