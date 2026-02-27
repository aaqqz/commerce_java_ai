package io.dodn.commerce.core.domain;

import io.dodn.commerce.core.support.error.CoreException;
import io.dodn.commerce.core.support.error.ErrorType;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class CouponDiscountCalculator {

    private CouponDiscountCalculator() {}

    public static BigDecimal calculate(Coupon coupon, BigDecimal orderAmount) {
        return switch (coupon.type()) {
            case FIXED_AMOUNT -> coupon.discount();
            case PERCENT_RATE -> calculatePercentRate(coupon, orderAmount);
        };
    }

    private static BigDecimal calculatePercentRate(Coupon coupon, BigDecimal orderAmount) {
        BigDecimal rate = coupon.discount();

        if (rate.compareTo(BigDecimal.ZERO) <= 0 || rate.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new CoreException(ErrorType.COUPON_INVALID_RATE);
        }

        if (coupon.minOrderAmount() != null
                && orderAmount.compareTo(coupon.minOrderAmount()) < 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal discountAmount = orderAmount
                .multiply(rate)
                .divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP);

        if (coupon.maxDiscountAmount() != null
                && discountAmount.compareTo(coupon.maxDiscountAmount()) > 0) {
            discountAmount = coupon.maxDiscountAmount();
        }

        return discountAmount;
    }
}
