package io.dodn.commerce.core.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class RateSummary {
    private final BigDecimal rate;
    private final Long count;

    public static final RateSummary EMPTY = new RateSummary(BigDecimal.ZERO, 0L);
}
