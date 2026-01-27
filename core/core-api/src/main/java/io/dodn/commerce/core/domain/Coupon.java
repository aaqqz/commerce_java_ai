package io.dodn.commerce.core.domain;

import io.dodn.commerce.core.enums.CouponType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class Coupon {
    private final Long id;
    private final String name;
    private final CouponType type;
    private final BigDecimal discount;
    private final LocalDateTime expiredAt;
}
