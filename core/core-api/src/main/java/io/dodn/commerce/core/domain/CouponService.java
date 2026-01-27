package io.dodn.commerce.core.domain;

import io.dodn.commerce.core.enums.CouponTargetType;
import io.dodn.commerce.core.enums.EntityStatus;
import io.dodn.commerce.storage.db.core.CouponRepository;
import io.dodn.commerce.storage.db.core.CouponTargetEntity;
import io.dodn.commerce.storage.db.core.CouponTargetRepository;
import io.dodn.commerce.storage.db.core.ProductCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class CouponService {
    private final CouponRepository couponRepository;
    private final CouponTargetRepository couponTargetRepository;
    private final ProductCategoryRepository productCategoryRepository;

    public List<Coupon> getCouponsForProducts(Collection<Long> productIds) {
        List<CouponTargetEntity> productTargets = couponTargetRepository.findByTargetTypeAndTargetIdInAndStatus(
                CouponTargetType.PRODUCT,
                productIds,
                EntityStatus.ACTIVE
        );
        List<CouponTargetEntity> categoryTargets = couponTargetRepository.findByTargetTypeAndTargetIdInAndStatus(
                CouponTargetType.PRODUCT_CATEGORY,
                productCategoryRepository.findByProductIdInAndStatus(productIds, EntityStatus.ACTIVE)
                        .stream()
                        .map(io.dodn.commerce.storage.db.core.ProductCategoryEntity::getCategoryId)
                        .collect(Collectors.toList()),
                EntityStatus.ACTIVE
        );

        Set<Long> couponIds = Stream.concat(productTargets.stream(), categoryTargets.stream())
                .map(CouponTargetEntity::getCouponId)
                .collect(Collectors.toSet());

        return couponRepository.findByIdInAndStatus(couponIds, EntityStatus.ACTIVE).stream()
                .map(it -> new Coupon(
                        it.getId(),
                        it.getName(),
                        it.getType(),
                        it.getDiscount(),
                        it.getExpiredAt()
                ))
                .collect(Collectors.toList());
    }
}
