package io.dodn.commerce.core.domain;

import java.math.BigDecimal;

public record PointBalance(
        Long userId,
        BigDecimal balance
) {
}
