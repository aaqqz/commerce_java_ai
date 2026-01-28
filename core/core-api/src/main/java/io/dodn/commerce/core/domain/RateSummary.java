package io.dodn.commerce.core.domain;

import java.math.BigDecimal;

public record RateSummary(
        BigDecimal rate,
        Long count
) {
    public static final RateSummary EMPTY = new RateSummary(BigDecimal.ZERO, 0L);
}
