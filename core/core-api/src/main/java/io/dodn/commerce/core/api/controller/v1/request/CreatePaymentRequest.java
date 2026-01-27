package io.dodn.commerce.core.api.controller.v1.request;

import io.dodn.commerce.core.domain.OwnedCoupon;
import io.dodn.commerce.core.domain.PaymentDiscount;
import io.dodn.commerce.core.domain.PointBalance;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreatePaymentRequest {
    private String orderKey;
    private Long useOwnedCouponId;
    private BigDecimal usePoint;

    public PaymentDiscount toPaymentDiscount(List<OwnedCoupon> ownedCoupons, PointBalance pointBalance) {
        return new PaymentDiscount(
                ownedCoupons,
                pointBalance,
                useOwnedCouponId != null ? useOwnedCouponId : -1L,
                usePoint != null ? usePoint : BigDecimal.valueOf(-1)
        );
    }
}
