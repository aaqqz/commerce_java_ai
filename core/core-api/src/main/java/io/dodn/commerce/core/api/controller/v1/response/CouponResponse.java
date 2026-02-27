package io.dodn.commerce.core.api.controller.v1.response;

import io.dodn.commerce.core.domain.Coupon;
import io.dodn.commerce.core.enums.CouponType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CouponResponse(
        Long id,
        String name,
        CouponType type,
        BigDecimal discount,
        LocalDateTime expiredAt,
        BigDecimal maxDiscountAmount,
        BigDecimal minOrderAmount
) {
    public static CouponResponse of(Coupon coupon) {
        return new CouponResponse(
                coupon.id(),
                coupon.name(),
                coupon.type(),
                coupon.discount(),
                coupon.expiredAt(),
                coupon.maxDiscountAmount(),
                coupon.minOrderAmount()
        );
    }

    public static List<CouponResponse> of(List<Coupon> coupons) {
        return coupons.stream()
                .map(CouponResponse::of)
                .toList();
    }
}
