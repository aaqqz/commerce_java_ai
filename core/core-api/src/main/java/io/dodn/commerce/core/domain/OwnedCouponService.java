package io.dodn.commerce.core.domain;

import io.dodn.commerce.core.enums.EntityStatus;
import io.dodn.commerce.core.enums.OwnedCouponState;
import io.dodn.commerce.core.support.error.CoreException;
import io.dodn.commerce.core.support.error.ErrorType;
import io.dodn.commerce.storage.db.core.CouponEntity;
import io.dodn.commerce.storage.db.core.CouponRepository;
import io.dodn.commerce.storage.db.core.OwnedCouponEntity;
import io.dodn.commerce.storage.db.core.OwnedCouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OwnedCouponService {
    private final CouponRepository couponRepository;
    private final OwnedCouponRepository ownedCouponRepository;

    public List<OwnedCoupon> getOwnedCoupons(User user) {
        List<OwnedCouponEntity> ownedCoupons = ownedCouponRepository.findByUserIdAndStatus(user.getId(), EntityStatus.ACTIVE);
        if (ownedCoupons.isEmpty()) {
            return List.of();
        }

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
                .collect(Collectors.toList());
    }

    public void download(User user, Long couponId) {
        CouponEntity coupon = couponRepository.findByIdAndStatusAndExpiredAtAfter(couponId, EntityStatus.ACTIVE, LocalDateTime.now())
                .orElseThrow(() -> new CoreException(ErrorType.COUPON_NOT_FOUND_OR_EXPIRED));

        ownedCouponRepository.findByUserIdAndCouponId(user.getId(), couponId)
                .orElseThrow(() -> new CoreException(ErrorType.COUPON_ALREADY_DOWNLOADED));

        ownedCouponRepository.save(OwnedCouponEntity.create(
                user.getId(),
                coupon.getId(),
                OwnedCouponState.DOWNLOADED
        ));
    }

    public List<OwnedCoupon> getOwnedCouponsForCheckout(User user, Collection<Long> productIds) {
        if (productIds.isEmpty()) {
            return List.of();
        }

        Map<Long, CouponEntity> applicableCouponMap = couponRepository.findApplicableCouponIds(productIds)
                .stream()
                .collect(Collectors.toMap(CouponEntity::getId, c -> c));

        if (applicableCouponMap.isEmpty()) {
            return List.of();
        }

        List<OwnedCouponEntity> ownedCoupons = ownedCouponRepository.findOwnedCouponIds(
                user.getId(), applicableCouponMap.keySet(), LocalDateTime.now());

        if (ownedCoupons.isEmpty()) {
            return List.of();
        }

        return ownedCoupons.stream()
                .map(it -> {
                    CouponEntity coupon = applicableCouponMap.get(it.getCouponId());
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
                .collect(Collectors.toList());
    }
}
