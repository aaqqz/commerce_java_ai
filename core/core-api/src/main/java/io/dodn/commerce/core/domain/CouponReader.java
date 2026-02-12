package io.dodn.commerce.core.domain;

import io.dodn.commerce.core.enums.EntityStatus;
import io.dodn.commerce.storage.db.core.CouponEntity;
import io.dodn.commerce.storage.db.core.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CouponReader {
    private final CouponRepository couponRepository;

    public List<Coupon> findActiveByIds(Collection<Long> couponIds) {
        if (couponIds.isEmpty()) return List.of();

        // Coupon 조회 및 매핑
        return couponRepository.findByIdInAndStatus(couponIds, EntityStatus.ACTIVE).stream()
                .map(it -> new Coupon(
                        it.getId(),
                        it.getName(),
                        it.getType(),
                        it.getDiscount(),
                        it.getExpiredAt()
                ))
                .toList();
    }
}
