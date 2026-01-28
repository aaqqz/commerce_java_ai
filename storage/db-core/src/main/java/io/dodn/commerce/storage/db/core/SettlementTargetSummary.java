package io.dodn.commerce.storage.db.core;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SettlementTargetSummary(
        Long merchantId,
        LocalDate settlementDate,
        BigDecimal targetAmount,
        Long targetCount,
        Long orderCount
) {
}
