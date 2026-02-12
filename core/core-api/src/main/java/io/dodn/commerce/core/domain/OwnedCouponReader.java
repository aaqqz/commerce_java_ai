package io.dodn.commerce.core.domain;

import io.dodn.commerce.core.enums.EntityStatus;
import io.dodn.commerce.storage.db.core.CouponEntity;
import io.dodn.commerce.storage.db.core.CouponRepository;
import io.dodn.commerce.storage.db.core.OwnedCouponEntity;
import io.dodn.commerce.storage.db.core.OwnedCouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OwnedCouponReader {
    private final OwnedCouponRepository ownedCouponRepository;
    private final CouponRepository couponRepository;

    public List<OwnedCoupon> getOwnedCoupons(Long userId) {
        List<OwnedCouponEntity> ownedCoupons = ownedCouponRepository.findByUserIdAndStatus(userId, EntityStatus.ACTIVE);
        if (ownedCoupons.isEmpty()) return List.of();

        Map<Long, CouponEntity> couponMap = couponRepository.findAllById(
                ownedCoupons.stream()
                    .map(OwnedCouponEntity::getCouponId)
                    .collect(Collectors.toSet())
            ).stream()
            .collect(Collectors.toMap(CouponEntity::getId, c -> c));

        return ownedCoupons.stream()
                .map(it -> {
                    CouponEntity coupon = couponMap.get(it.getCouponId());
                    return new OwnedCoupon(
                            it.getId(),
                            it.getUserId(),
                            it.getState(),
                            new Coupon(
                                    coupon.getId(),
                                    coupon.getName(),
                                    coupon.getType(),
                                    coupon.getDiscount(),
                                    coupon.getExpiredAt()
                            )
                    );
                })
                .toList();
    }

    /**
     * 특정 쿠폰 ID들로 필터링된 소유 쿠폰 조회
     * (체크아웃 시 적용 가능한 쿠폰 조회용)
     */
    public List<OwnedCoupon> findOwnedForCheckout(User user, Collection<Long> couponIds, LocalDateTime now) {
        if (couponIds.isEmpty()) return List.of();


        List<OwnedCouponEntity> ownedCoupons = ownedCouponRepository.findOwnedCouponIds(user.id(), couponIds, now);
        if (ownedCoupons.isEmpty()) return List.of();

        Map<Long, CouponEntity> couponMap = couponRepository.findAllById(
                ownedCoupons.stream()
                    .map(OwnedCouponEntity::getCouponId)
                    .collect(Collectors.toSet())
            ).stream()
            .collect(Collectors.toMap(CouponEntity::getId, c -> c));

        return ownedCoupons.stream()
                .map(it -> {
                    CouponEntity coupon = couponMap.get(it.getCouponId());
                    return new OwnedCoupon(
                            it.getId(),
                            it.getUserId(),
                            it.getState(),
                            new Coupon(
                                    coupon.getId(),
                                    coupon.getName(),
                                    coupon.getType(),
                                    coupon.getDiscount(),
                                    coupon.getExpiredAt()
                            )
                    );
                })
                .toList();
    }
}
