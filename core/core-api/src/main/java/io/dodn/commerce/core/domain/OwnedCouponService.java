package io.dodn.commerce.core.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class OwnedCouponService {
    private final OwnedCouponReader ownedCouponReader;
    private final CouponDownloader couponDownloader;
    private final CouponTargetReader couponTargetReader;

    public List<OwnedCoupon> getOwnedCoupons(User user) {
        return ownedCouponReader.getOwnedCoupons(user.id());
    }

    public void download(User user, Long couponId) {
        couponDownloader.download(user.id(), couponId);
    }

    public List<OwnedCoupon> getOwnedCouponsForCheckout(User user, Collection<Long> productIds) {
        if (productIds.isEmpty()) return List.of();

        // 1. 상품에 적용 가능한 쿠폰 ID 조회 (타겟팅 로직)
        Set<Long> applicableCouponIds = couponTargetReader.findCouponIdsByProductIds(productIds);
        if (applicableCouponIds.isEmpty()) return List.of();

        // 2. 사용자가 소유한 쿠폰 중 적용 가능한 쿠폰 조회
        return ownedCouponReader.findOwnedForCheckout(user, applicableCouponIds, LocalDateTime.now());
    }
}
