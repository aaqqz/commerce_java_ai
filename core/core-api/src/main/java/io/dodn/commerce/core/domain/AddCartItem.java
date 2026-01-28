package io.dodn.commerce.core.domain;

public record AddCartItem(
        Long productId,
        Long quantity
) {
}
