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
        LocalDateTime expiredAt
) {
    public static CouponResponse of(Coupon coupon) {
        return new CouponResponse(
                coupon.getId(),
                coupon.getName(),
                coupon.getType(),
                coupon.getDiscount(),
                coupon.getExpiredAt()
        );
    }

    public static List<CouponResponse> of(List<Coupon> coupons) {
        return coupons.stream().map(CouponResponse::of).toList();
    }
}
