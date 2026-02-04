package io.dodn.commerce.core.domain;

public record ProductOption(
        Long id,
        Long productId,
        String name,
        String description,
        Price price
) {
}
