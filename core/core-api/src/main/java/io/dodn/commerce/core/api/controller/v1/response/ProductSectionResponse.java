package io.dodn.commerce.core.api.controller.v1.response;

import io.dodn.commerce.core.enums.ProductSectionType;

public record ProductSectionResponse(
        ProductSectionType type,
        String content
) {
}
