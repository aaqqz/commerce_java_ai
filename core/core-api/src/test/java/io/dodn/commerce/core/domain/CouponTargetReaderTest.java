package io.dodn.commerce.core.domain;

import io.dodn.commerce.ContextTest;
import io.dodn.commerce.core.enums.CouponTargetType;
import io.dodn.commerce.core.enums.CouponType;
import io.dodn.commerce.storage.db.core.CouponEntity;
import io.dodn.commerce.storage.db.core.CouponRepository;
import io.dodn.commerce.storage.db.core.CouponTargetEntity;
import io.dodn.commerce.storage.db.core.CouponTargetRepository;
import io.dodn.commerce.storage.db.core.ProductCategoryEntity;
import io.dodn.commerce.storage.db.core.ProductCategoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class CouponTargetReaderTest extends ContextTest {

    private final CouponTargetReader couponTargetReader;
    private final CouponTargetRepository couponTargetRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final CouponRepository couponRepository;

    @Autowired
    public CouponTargetReaderTest(
            CouponTargetReader couponTargetReader,
            CouponTargetRepository couponTargetRepository,
            ProductCategoryRepository productCategoryRepository,
            CouponRepository couponRepository
    ) {
        this.couponTargetReader = couponTargetReader;
        this.couponTargetRepository = couponTargetRepository;
        this.productCategoryRepository = productCategoryRepository;
        this.couponRepository = couponRepository;
    }

    @Test
    @Transactional
    void 상품에_적용_가능한_쿠폰_ID가_조회_되어야한다() {
        // given
        LocalDateTime now = LocalDateTime.now();

        // c1: PRODUCT 타겟 ACTIVE → 반환 O
        CouponEntity c1 = couponRepository.save(
                CouponEntity.create("PRODUCT_ID_10", CouponType.FIXED_AMOUNT, BigDecimal.TEN, now.plusDays(7))
        );
        couponTargetRepository.save(
                CouponTargetEntity.create(c1.getId(), CouponTargetType.PRODUCT, 10L)
        );

        // c2: PRODUCT_CATEGORY 타겟 ACTIVE + 카테고리 매핑 ACTIVE → 반환 O
        CouponEntity c2 = couponRepository.save(
                CouponEntity.create("PRODUCT_CATEGORY_100", CouponType.FIXED_AMOUNT, BigDecimal.ONE, now.plusDays(3))
        );
        couponTargetRepository.save(
                CouponTargetEntity.create(c2.getId(), CouponTargetType.PRODUCT_CATEGORY, 100L)
        );
        productCategoryRepository.save(ProductCategoryEntity.create(11L, 100L));

        // c3: 쿠폰 DELETED이지만 PRODUCT 타겟 ACTIVE → 반환 O (쿠폰 상태는 이 컴포넌트 책임 아님)
        CouponEntity c3 = couponRepository.save(
                CouponEntity.create("INACTIVE_COUPON_DELETED", CouponType.FIXED_AMOUNT, BigDecimal.valueOf(5), now.plusDays(5))
        );
        c3.delete();
        couponRepository.save(c3);
        couponTargetRepository.save(
                CouponTargetEntity.create(c3.getId(), CouponTargetType.PRODUCT, 12L)
        );

        // c4: PRODUCT + PRODUCT_CATEGORY 동시 매칭 → 중복 없이 1개 반환 O
        CouponEntity c4 = couponRepository.save(
                CouponEntity.create("BOTH_PRODUCT_12_PRODUCT_CATEGORY_200", CouponType.FIXED_AMOUNT, BigDecimal.valueOf(20), now.plusDays(10))
        );
        couponTargetRepository.saveAll(
                Arrays.asList(
                        CouponTargetEntity.create(c4.getId(), CouponTargetType.PRODUCT, 12L),
                        CouponTargetEntity.create(c4.getId(), CouponTargetType.PRODUCT_CATEGORY, 200L)
                )
        );
        productCategoryRepository.save(ProductCategoryEntity.create(12L, 200L));

        // c5: PRODUCT 타겟 DELETED → 반환 X
        CouponEntity c5 = couponRepository.save(
                CouponEntity.create("INACTIVE_COUPON_TARGET_DELETED", CouponType.FIXED_AMOUNT, BigDecimal.valueOf(7), now.plusDays(2))
        );
        CouponTargetEntity inactiveTarget = couponTargetRepository.save(
                CouponTargetEntity.create(c5.getId(), CouponTargetType.PRODUCT, 13L)
        );
        inactiveTarget.delete();
        couponTargetRepository.save(inactiveTarget);

        // target 없는 쿠폰 → 반환 X
        couponRepository.save(
                CouponEntity.create("NOT_MATCH_PRODUCT", CouponType.FIXED_AMOUNT, BigDecimal.ONE, now.plusDays(1))
        );

        // when
        Set<Long> result = couponTargetReader.findCouponIdsByProductIds(Arrays.asList(10L, 11L, 12L, 13L));

        // then
        assertThat(result).containsExactlyInAnyOrder(c1.getId(), c2.getId(), c3.getId(), c4.getId());
    }

    @Test
    @Transactional
    void 비활성_타겟은_쿠폰_적용_대상에서_제외_되어야한다() {
        // given
        LocalDateTime now = LocalDateTime.now();

        CouponEntity coupon = couponRepository.save(
                CouponEntity.create("COUPON", CouponType.FIXED_AMOUNT, BigDecimal.TEN, now.plusDays(7))
        );
        CouponTargetEntity target = couponTargetRepository.save(
                CouponTargetEntity.create(coupon.getId(), CouponTargetType.PRODUCT, 10L)
        );
        target.delete();
        couponTargetRepository.save(target);

        // when
        Set<Long> result = couponTargetReader.findCouponIdsByProductIds(Arrays.asList(10L));

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @Transactional
    void 비활성_카테고리_매핑은_쿠폰_적용_대상에서_제외_되어야한다() {
        // given
        LocalDateTime now = LocalDateTime.now();

        CouponEntity coupon = couponRepository.save(
                CouponEntity.create("CATEGORY_COUPON", CouponType.FIXED_AMOUNT, BigDecimal.TEN, now.plusDays(7))
        );
        couponTargetRepository.save(
                CouponTargetEntity.create(coupon.getId(), CouponTargetType.PRODUCT_CATEGORY, 100L)
        );
        ProductCategoryEntity mapping = productCategoryRepository.save(ProductCategoryEntity.create(10L, 100L));
        mapping.delete();
        productCategoryRepository.save(mapping);

        // when
        Set<Long> result = couponTargetReader.findCouponIdsByProductIds(Arrays.asList(10L));

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void 빈_상품_목록으로_조회하면_빈_Set이_반환_되어야한다() {
        // when
        Set<Long> result = couponTargetReader.findCouponIdsByProductIds(Arrays.asList());

        // then
        assertThat(result).isEmpty();
    }
}
