package io.dodn.commerce.core.domain;

public record CartItem(
        Long id,
        Product product,
        Long quantity
) {
}
