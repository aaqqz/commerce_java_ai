package io.dodn.commerce.core.api.controller.v1.response;

import java.math.BigDecimal;

public record ProductOptionResponse(
        Long id,
        String name,
        String description,
        BigDecimal costPrice,
        BigDecimal salesPrice,
        BigDecimal discountedPrice
) {
}