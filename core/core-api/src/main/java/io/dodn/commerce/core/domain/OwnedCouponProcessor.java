package io.dodn.commerce.core.domain;

import io.dodn.commerce.core.support.error.CoreException;
import io.dodn.commerce.core.support.error.ErrorType;
import io.dodn.commerce.storage.db.core.OwnedCouponEntity;
import io.dodn.commerce.storage.db.core.OwnedCouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class OwnedCouponProcessor {
    private final OwnedCouponRepository ownedCouponRepository;

    @Transactional
    public void useOne(Long ownedCouponId) {
        OwnedCouponEntity entity = findOrThrow(ownedCouponId);
        if (entity.remainingUses() <= 0) {
            throw new CoreException(ErrorType.OWNED_COUPON_NO_REMAINING_USES);
        }
        entity.useOne(); // @Version 낙관적 락으로 동시성 보호
    }

    @Transactional
    public void revertOne(Long ownedCouponId) {
        findOrThrow(ownedCouponId).revertOne();
    }

    private OwnedCouponEntity findOrThrow(Long ownedCouponId) {
        return ownedCouponRepository.findById(ownedCouponId)
                .orElseThrow(() -> new CoreException(ErrorType.OWNED_COUPON_INVALID));
    }
}
