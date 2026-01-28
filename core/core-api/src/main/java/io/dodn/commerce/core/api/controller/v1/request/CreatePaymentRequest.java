package io.dodn.commerce.core.api.controller.v1.request;

import io.dodn.commerce.core.domain.OwnedCoupon;
import io.dodn.commerce.core.domain.PaymentDiscount;
import io.dodn.commerce.core.domain.PointBalance;

import java.math.BigDecimal;
import java.util.List;

public record CreatePaymentRequest(
        String orderKey,
        Long useOwnedCouponId,
        BigDecimal usePoint
) {
    public PaymentDiscount toPaymentDiscount(List<OwnedCoupon> ownedCoupons, PointBalance pointBalance) {
        return new PaymentDiscount(
                ownedCoupons,
                pointBalance,
                useOwnedCouponId != null ? useOwnedCouponId : -1L,
                usePoint != null ? usePoint : BigDecimal.valueOf(-1)
        );
    }
}
