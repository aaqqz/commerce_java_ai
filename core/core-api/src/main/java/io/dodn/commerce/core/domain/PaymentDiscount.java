package io.dodn.commerce.core.domain;

import io.dodn.commerce.core.support.error.CoreException;
import io.dodn.commerce.core.support.error.ErrorType;

import java.math.BigDecimal;
import java.util.List;

public record PaymentDiscount(
        List<OwnedCoupon> ownedCoupons,
        PointBalance pointBalance,
        Long useOwnedCouponId,
        BigDecimal usePointAmount,
        BigDecimal couponDiscount,
        BigDecimal usePoint
) {
    public PaymentDiscount(
            List<OwnedCoupon> ownedCoupons,
            PointBalance pointBalance,
            Long useOwnedCouponId,
            BigDecimal usePointAmount
    ) {
        this(
                ownedCoupons,
                pointBalance,
                useOwnedCouponId,
                usePointAmount,
                calculateCouponDiscount(ownedCoupons, useOwnedCouponId),
                calculateUsePoint(pointBalance, usePointAmount)
        );
    }

    private static BigDecimal calculateCouponDiscount(List<OwnedCoupon> ownedCoupons, Long useOwnedCouponId) {
        if (useOwnedCouponId <= 0) {
            return BigDecimal.ZERO;
        }

        OwnedCoupon ownedCoupon = ownedCoupons.stream()
                .filter(c -> c.id().equals(useOwnedCouponId))
                .findFirst()
                .orElseThrow(() -> new CoreException(ErrorType.OWNED_COUPON_INVALID));
        return ownedCoupon.coupon().discount();
    }

    private static BigDecimal calculateUsePoint(PointBalance pointBalance, BigDecimal usePointAmount) {
        if (usePointAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        if (usePointAmount.compareTo(pointBalance.balance()) > 0) {
            throw new CoreException(ErrorType.POINT_EXCEEDS_BALANCE);
        }
        return usePointAmount;
    }

    public BigDecimal paidAmount(BigDecimal orderPrice) {
        BigDecimal amount = orderPrice.subtract(couponDiscount.add(usePointAmount));
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new CoreException(ErrorType.PAYMENT_INVALID_AMOUNT);
        }
        return amount;
    }
}
