package io.dodn.commerce.core.domain;

import io.dodn.commerce.core.enums.CouponType;
import io.dodn.commerce.core.support.error.CoreException;
import io.dodn.commerce.core.support.error.ErrorType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CouponDiscountCalculatorTest {

    private Coupon fixedCoupon(BigDecimal discount) {
        return new Coupon(1L, "정액쿠폰", CouponType.FIXED_AMOUNT, discount,
                LocalDateTime.now().plusDays(7), null, null, 1);
    }

    private Coupon percentCoupon(BigDecimal rate, BigDecimal maxDiscountAmount, BigDecimal minOrderAmount) {
        return new Coupon(2L, "정률쿠폰", CouponType.PERCENT_RATE, rate,
                LocalDateTime.now().plusDays(7), maxDiscountAmount, minOrderAmount, 1);
    }

    @Test
    void FIXED_AMOUNT_쿠폰은_discount_금액을_그대로_반환한다() {
        Coupon coupon = fixedCoupon(BigDecimal.valueOf(1000));

        BigDecimal result = CouponDiscountCalculator.calculate(coupon, BigDecimal.valueOf(50000));

        assertThat(result).isEqualByComparingTo(BigDecimal.valueOf(1000));
    }

    @Test
    void PERCENT_RATE_기본_계산_HALF_UP_반올림() {
        // rate=10, order=9999 → 999.9 → 반올림 → 1000
        Coupon coupon = percentCoupon(BigDecimal.valueOf(10), null, null);

        BigDecimal result = CouponDiscountCalculator.calculate(coupon, BigDecimal.valueOf(9999));

        assertThat(result).isEqualByComparingTo(BigDecimal.valueOf(1000));
    }

    @Test
    void PERCENT_RATE_상한액_적용() {
        // rate=50, max=3000, order=10000 → 5000 > 3000 → 3000
        Coupon coupon = percentCoupon(BigDecimal.valueOf(50), BigDecimal.valueOf(3000), null);

        BigDecimal result = CouponDiscountCalculator.calculate(coupon, BigDecimal.valueOf(10000));

        assertThat(result).isEqualByComparingTo(BigDecimal.valueOf(3000));
    }

    @Test
    void PERCENT_RATE_상한액_미만() {
        // rate=10, max=5000, order=10000 → 1000 < 5000 → 1000
        Coupon coupon = percentCoupon(BigDecimal.valueOf(10), BigDecimal.valueOf(5000), null);

        BigDecimal result = CouponDiscountCalculator.calculate(coupon, BigDecimal.valueOf(10000));

        assertThat(result).isEqualByComparingTo(BigDecimal.valueOf(1000));
    }

    @Test
    void PERCENT_RATE_최소주문금액_미충족_시_0_반환() {
        // min=20000, order=15000 → 0
        Coupon coupon = percentCoupon(BigDecimal.valueOf(10), null, BigDecimal.valueOf(20000));

        BigDecimal result = CouponDiscountCalculator.calculate(coupon, BigDecimal.valueOf(15000));

        assertThat(result).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void PERCENT_RATE_최소주문금액_정확히_충족_시_계산됨() {
        // min=10000, order=10000 → 계산됨 (10% of 10000 = 1000)
        Coupon coupon = percentCoupon(BigDecimal.valueOf(10), null, BigDecimal.valueOf(10000));

        BigDecimal result = CouponDiscountCalculator.calculate(coupon, BigDecimal.valueOf(10000));

        assertThat(result).isEqualByComparingTo(BigDecimal.valueOf(1000));
    }

    @Test
    void PERCENT_RATE_rate_100_경계값() {
        // rate=100, order=5000 → 5000
        Coupon coupon = percentCoupon(BigDecimal.valueOf(100), null, null);

        BigDecimal result = CouponDiscountCalculator.calculate(coupon, BigDecimal.valueOf(5000));

        assertThat(result).isEqualByComparingTo(BigDecimal.valueOf(5000));
    }

    @Test
    void PERCENT_RATE_rate_0_이면_예외() {
        Coupon coupon = percentCoupon(BigDecimal.ZERO, null, null);

        assertThatThrownBy(() -> CouponDiscountCalculator.calculate(coupon, BigDecimal.valueOf(10000)))
                .isInstanceOf(CoreException.class)
                .satisfies(e -> assertThat(((CoreException) e).getErrorType())
                        .isEqualTo(ErrorType.COUPON_INVALID_RATE));
    }

    @Test
    void PERCENT_RATE_rate_101_이면_예외() {
        Coupon coupon = percentCoupon(BigDecimal.valueOf(101), null, null);

        assertThatThrownBy(() -> CouponDiscountCalculator.calculate(coupon, BigDecimal.valueOf(10000)))
                .isInstanceOf(CoreException.class)
                .satisfies(e -> assertThat(((CoreException) e).getErrorType())
                        .isEqualTo(ErrorType.COUPON_INVALID_RATE));
    }
}
