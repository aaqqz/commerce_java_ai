package io.dodn.commerce.core.domain;

import java.math.BigDecimal;

public final class PointAmount {
    private PointAmount() {}

    public static final BigDecimal REVIEW = BigDecimal.valueOf(1000);
    public static final BigDecimal PAYMENT = BigDecimal.valueOf(2000);
}
