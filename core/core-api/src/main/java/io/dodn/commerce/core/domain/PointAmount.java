package io.dodn.commerce.core.domain;

import java.math.BigDecimal;

public final class PointAmount {
    private PointAmount() {}

    public static final BigDecimal TEXT_REVIEW = BigDecimal.valueOf(1000);
    public static final BigDecimal IMAGE_REVIEW = BigDecimal.valueOf(1500);
    public static final BigDecimal PAYMENT = BigDecimal.valueOf(2000);
}
