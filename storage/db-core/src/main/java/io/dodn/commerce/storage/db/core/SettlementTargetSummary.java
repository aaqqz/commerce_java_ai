package io.dodn.commerce.storage.db.core;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class SettlementTargetSummary {
    private final Long merchantId;
    private final LocalDate settlementDate;
    private final BigDecimal targetAmount;
    private final Long targetCount;
    private final Long orderCount;
}
