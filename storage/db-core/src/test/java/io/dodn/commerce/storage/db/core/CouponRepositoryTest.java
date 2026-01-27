package io.dodn.commerce.storage.db.core;

import io.dodn.commerce.core.enums.CouponTargetType;
import io.dodn.commerce.core.enums.CouponType;
import io.dodn.commerce.storage.db.CoreDbContextTest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class CouponRepositoryTest extends CoreDbContextTest {

    private final CouponRepository couponRepository;
    private final CouponTargetRepository couponTargetRepository;
    private final ProductCategoryRepository productCategoryRepository;

    public CouponRepositoryTest(
            CouponRepository couponRepository,
            CouponTargetRepository couponTargetRepository,
            ProductCategoryRepository productCategoryRepository) {
        this.couponRepository = couponRepository;
        this.couponTargetRepository = couponTargetRepository;
        this.productCategoryRepository = productCategoryRepository;
    }

    @Test
    void 활성_쿠폰_조회가_되어야한다() {
        // given
        LocalDateTime now = LocalDateTime.now();

        // 상품 10에 직접 적용되는 쿠폰
        CouponEntity c1 = couponRepository.save(
                new CouponEntity(
                        "PRODUCT_ID_10",
                        CouponType.FIXED_AMOUNT,
                        BigDecimal.TEN,
                        now.plusDays(7)
                )
        );
        couponTargetRepository.save(
                new CouponTargetEntity(
                        c1.getId(),
                        CouponTargetType.PRODUCT,
                        10L
                )
        );

        // 카테고리 100에 적용되는 쿠폰, 상품 11은 카테고리 100에 속함
        CouponEntity c2 = couponRepository.save(
                new CouponEntity(
                        "PRODUCT_CATEGORY_100",
                        CouponType.FIXED_AMOUNT,
                        BigDecimal.ONE,
                        now.plusDays(3)
                )
        );
        couponTargetRepository.save(
                new CouponTargetEntity(
                        c2.getId(),
                        CouponTargetType.PRODUCT_CATEGORY,
                        100L
                )
        );
        productCategoryRepository.save(
                new ProductCategoryEntity(
                        11L,
                        100L
                )
        );

        // 매치될 수 있지만 삭제(비활성)된 쿠폰
        CouponEntity c3 = couponRepository.save(
                new CouponEntity(
                        "INACTIVE_COUPON_DELETED",
                        CouponType.FIXED_AMOUNT,
                        BigDecimal.valueOf(5),
                        now.plusDays(5)
                )
        );
        c3.delete();
        couponRepository.save(c3);
        couponTargetRepository.save(
                new CouponTargetEntity(
                        c3.getId(),
                        CouponTargetType.PRODUCT,
                        12L
                )
        );

        // 동일 상품에 대해 상품과 카테고리 둘 다 적용되는 쿠폰 (중복 없이 유니크하게 처리되어야 함)
        CouponEntity c4 = couponRepository.save(
                new CouponEntity(
                        "BOTH_PRODUCT_12_PRODUCT_CATEGORY_200",
                        CouponType.FIXED_AMOUNT,
                        BigDecimal.valueOf(20),
                        now.plusDays(10)
                )
        );
        couponTargetRepository.saveAll(
                Arrays.asList(
                        new CouponTargetEntity(
                                c4.getId(),
                                CouponTargetType.PRODUCT,
                                12L
                        ),
                        new CouponTargetEntity(
                                c4.getId(),
                                CouponTargetType.PRODUCT_CATEGORY,
                                200L
                        )
                )
        );
        productCategoryRepository.save(
                new ProductCategoryEntity(
                        12L,
                        200L
                )
        );

        // 비활성 타깃은 쿠폰 적용 대상이 아님
        CouponEntity c5 = couponRepository.save(
                new CouponEntity(
                        "INACTIVE_COUPON_TARGET_DELETED",
                        CouponType.FIXED_AMOUNT,
                        BigDecimal.valueOf(7),
                        now.plusDays(2)
                )
        );
        CouponTargetEntity inactiveTarget = couponTargetRepository.save(
                new CouponTargetEntity(
                        c5.getId(),
                        CouponTargetType.PRODUCT,
                        13L
                )
        );
        inactiveTarget.delete();
        couponTargetRepository.save(inactiveTarget);

        // 쿠폰만 존재하는 데이터
        couponRepository.save(
                new CouponEntity(
                        "NOT_MATCH_PRODUCT",
                        CouponType.FIXED_AMOUNT,
                        BigDecimal.ONE,
                        now.plusDays(1)
                )
        );

        // when
        List<CouponEntity> result = couponRepository.findApplicableCouponIds(Arrays.asList(10L, 11L, 12L, 13L));

        // then
        Set<Long> resultIds = result.stream().map(CouponEntity::getId).collect(Collectors.toSet());
        assertThat(resultIds).containsExactlyInAnyOrder(c1.getId(), c2.getId(), c4.getId());
    }
}
