package io.dodn.commerce.core.domain;

import io.dodn.commerce.core.enums.CouponType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Coupon(
        Long id,
        String name,
        CouponType type,
        BigDecimal discount,
        LocalDateTime expiredAt
) {
}
