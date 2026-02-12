package io.dodn.commerce.core.domain;

import io.dodn.commerce.core.enums.CouponTargetType;
import io.dodn.commerce.core.enums.EntityStatus;
import io.dodn.commerce.storage.db.core.CouponTargetEntity;
import io.dodn.commerce.storage.db.core.CouponTargetRepository;
import io.dodn.commerce.storage.db.core.ProductCategoryEntity;
import io.dodn.commerce.storage.db.core.ProductCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class CouponTargetReader {
    private final CouponTargetRepository couponTargetRepository;
    private final ProductCategoryRepository productCategoryRepository;

    public Set<Long> findCouponIdsByProductIds(Collection<Long> productIds) {
        if (productIds.isEmpty()) return Set.of();

        // PRODUCT 타입 타겟 조회
        List<CouponTargetEntity> productTargets = couponTargetRepository.findByTargetTypeAndTargetIdInAndStatus(
                CouponTargetType.PRODUCT,
                productIds,
                EntityStatus.ACTIVE
        );

        // PRODUCT_CATEGORY 대상 쿠폰 조회
        List<Long> categoryIds = productCategoryRepository.findByProductIdInAndStatus(productIds, EntityStatus.ACTIVE).stream()
                .map(ProductCategoryEntity::getCategoryId)
                .toList();

        List<CouponTargetEntity> categoryTargets;
        if (categoryIds.isEmpty()) {
            categoryTargets = List.of();
        } else {
            categoryTargets = couponTargetRepository.findByTargetTypeAndTargetIdInAndStatus(
                    CouponTargetType.PRODUCT_CATEGORY,
                    categoryIds,
                    EntityStatus.ACTIVE
            );
        }

        // 두 리스트 합치고 couponId만 추출
        return Stream.concat(productTargets.stream(), categoryTargets.stream())
                .map(CouponTargetEntity::getCouponId)
                .collect(Collectors.toSet());
    }
}
