package io.dodn.commerce.core.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponService {
    private final CouponTargetReader couponTargetReader;
    private final CouponReader couponReader;

    public List<Coupon> getCouponsForProducts(Collection<Long> productIds) {
        var couponIds = couponTargetReader.findCouponIdsByProductIds(productIds);
        return couponReader.findActiveByIds(couponIds);
    }
}
