package io.dodn.commerce.core.domain;

import io.dodn.commerce.core.enums.ProductSectionType;

public record ProductSection(
        ProductSectionType type,
        String content
) {
}
