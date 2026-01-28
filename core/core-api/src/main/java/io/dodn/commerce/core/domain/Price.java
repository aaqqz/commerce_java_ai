package io.dodn.commerce.core.domain;

import java.math.BigDecimal;

public record Price(
        BigDecimal costPrice,
        BigDecimal salesPrice,
        BigDecimal discountedPrice
) {
}
