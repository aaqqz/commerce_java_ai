package io.dodn.commerce.core.api.controller.v1.response;

import io.dodn.commerce.core.domain.OwnedCoupon;
import io.dodn.commerce.core.enums.CouponType;
import io.dodn.commerce.core.enums.OwnedCouponState;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OwnedCouponResponse(
        Long id,
        OwnedCouponState state,
        String name,
        CouponType type,
        BigDecimal discount,
        LocalDateTime expiredAt
) {
    public static OwnedCouponResponse of(OwnedCoupon ownedCoupon) {
        return new OwnedCouponResponse(
                ownedCoupon.id(),
                ownedCoupon.state(),
                ownedCoupon.coupon().name(),
                ownedCoupon.coupon().type(),
                ownedCoupon.coupon().discount(),
                ownedCoupon.coupon().expiredAt()
        );
    }

    public static List<OwnedCouponResponse> of(List<OwnedCoupon> ownedCoupons) {
        return ownedCoupons.stream()
                .map(OwnedCouponResponse::of)
                .toList();
    }
}
