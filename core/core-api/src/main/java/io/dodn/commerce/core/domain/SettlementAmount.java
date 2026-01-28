package io.dodn.commerce.core.domain;

import java.math.BigDecimal;

public record SettlementAmount(
        BigDecimal originalAmount,
        BigDecimal feeAmount,
        BigDecimal feeRate,
        BigDecimal settlementAmount
) {
}
