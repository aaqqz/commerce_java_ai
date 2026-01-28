package io.dodn.commerce.core.domain;

import io.dodn.commerce.core.enums.CouponTargetType;
import io.dodn.commerce.core.enums.EntityStatus;
import io.dodn.commerce.storage.db.core.*;
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
        // PRODUCT 대상 쿠폰 조회
        List<CouponTargetEntity> productTargets = couponTargetRepository.findByTargetTypeAndTargetIdInAndStatus(
                CouponTargetType.PRODUCT,
                productIds,
                EntityStatus.ACTIVE
        );

        // PRODUCT_CATEGORY 대상 쿠폰 조회
        List<Long> productCategoryIds = productCategoryRepository.findByProductIdInAndStatus(productIds, EntityStatus.ACTIVE).stream()
                .map(ProductCategoryEntity::getCategoryId)
                .toList();

        List<CouponTargetEntity> categoryTargets = couponTargetRepository.findByTargetTypeAndTargetIdInAndStatus(
                CouponTargetType.PRODUCT_CATEGORY,
                productCategoryIds,
                EntityStatus.ACTIVE
        );

        // 두 리스트 합치고 couponId만 추출
        Set<Long> couponIds = Stream.concat(productTargets.stream(), categoryTargets.stream())
                .map(CouponTargetEntity::getCouponId)
                .collect(Collectors.toSet());

        // 최종 Coupon 조회 및 매핑
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
