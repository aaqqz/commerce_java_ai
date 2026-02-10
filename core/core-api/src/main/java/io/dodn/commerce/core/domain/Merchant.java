package io.dodn.commerce.core.domain;

public record Merchant(
        Long id,
        String name,
        String logoUrl,
        String description
) {
}
