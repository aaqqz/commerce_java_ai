package io.dodn.commerce.core.domain;

import io.dodn.commerce.core.enums.EntityStatus;
import io.dodn.commerce.core.support.error.CoreException;
import io.dodn.commerce.core.support.error.ErrorType;
import io.dodn.commerce.storage.db.core.CouponEntity;
import io.dodn.commerce.storage.db.core.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CouponDownloader {
    private final CouponRepository couponRepository;
    private final OwnedCouponAdder ownedCouponAdder;

    public void download(Long userId, Long couponId) {
        // 쿠폰 존재 및 만료 검증
        CouponEntity coupon = couponRepository
            .findByIdAndStatusAndExpiredAtAfter(
                couponId,
                EntityStatus.ACTIVE,
                LocalDateTime.now()
            )
            .orElseThrow(() -> new CoreException(ErrorType.COUPON_NOT_FOUND_OR_EXPIRED));

        // 소유 쿠폰 저장 (OwnedCouponAdder에 위임)
        ownedCouponAdder.addIfNotExists(userId, coupon.getId());
    }
}
