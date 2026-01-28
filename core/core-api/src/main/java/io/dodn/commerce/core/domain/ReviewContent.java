package io.dodn.commerce.core.domain;

import java.math.BigDecimal;

public record ReviewContent(
        BigDecimal rate,
        String content
) {
}
