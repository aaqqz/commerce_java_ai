package io.dodn.commerce.core.domain;

import io.dodn.commerce.core.enums.OwnedCouponState;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OwnedCoupon {
    private final Long id;
    private final Long userId;
    private final OwnedCouponState state;
    private final Coupon coupon;
}
