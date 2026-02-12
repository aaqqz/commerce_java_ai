package io.dodn.commerce.core.domain;

import io.dodn.commerce.core.enums.OwnedCouponState;
import io.dodn.commerce.core.support.error.CoreException;
import io.dodn.commerce.core.support.error.ErrorType;
import io.dodn.commerce.storage.db.core.OwnedCouponEntity;
import io.dodn.commerce.storage.db.core.OwnedCouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OwnedCouponAdder {
    private final OwnedCouponRepository ownedCouponRepository;

    public void addIfNotExists(Long userId, Long couponId) {
        ownedCouponRepository.findByUserIdAndCouponId(userId, couponId)
                .ifPresent(c -> {
                    throw new CoreException(ErrorType.COUPON_ALREADY_DOWNLOADED);
                });

        ownedCouponRepository.save(
            OwnedCouponEntity.create(userId, couponId, OwnedCouponState.DOWNLOADED)
        );
    }
}
