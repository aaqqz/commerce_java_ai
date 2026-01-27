package io.dodn.commerce.core.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class SettlementAmount {
    private final BigDecimal originalAmount;
    private final BigDecimal feeAmount;
    private final BigDecimal feeRate;
    private final BigDecimal settlementAmount;
}
