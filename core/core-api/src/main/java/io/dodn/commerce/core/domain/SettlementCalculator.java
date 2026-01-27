package io.dodn.commerce.core.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class SettlementCalculator {
    private SettlementCalculator() {}

    private static final BigDecimal FEE = BigDecimal.valueOf(0.1);

    public static SettlementAmount calculate(BigDecimal amount) {
        BigDecimal feeAmount = amount.multiply(FEE).setScale(2, RoundingMode.HALF_UP);

        return new SettlementAmount(
                amount,
                feeAmount,
                FEE,
                amount.subtract(feeAmount).setScale(2, RoundingMode.HALF_UP)
        );
    }
}
