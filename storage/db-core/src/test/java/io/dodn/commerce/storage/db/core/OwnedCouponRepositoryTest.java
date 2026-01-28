package io.dodn.commerce.storage.db.core;

import io.dodn.commerce.core.enums.CouponType;
import io.dodn.commerce.core.enums.OwnedCouponState;
import io.dodn.commerce.storage.db.CoreDbContextTest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class OwnedCouponRepositoryTest extends CoreDbContextTest {

    private final OwnedCouponRepository ownedCouponRepository;
    private final CouponRepository couponRepository;

    public OwnedCouponRepositoryTest(
            OwnedCouponRepository ownedCouponRepository,
            CouponRepository couponRepository) {
        this.ownedCouponRepository = ownedCouponRepository;
        this.couponRepository = couponRepository;
    }

    @Test
    void 조건에_맞는_소유_쿠폰만_조회_되어야한다() {
        // given
        Long userId = 100L;
        LocalDateTime now = LocalDateTime.now();

        // 활성 + 미만료 쿠폰 2개 생성
        CouponEntity activeValid1 = couponRepository.save(
                CouponEntity.create(
                        "ACTIVE_VALID_1",
                        CouponType.FIXED_AMOUNT,
                        BigDecimal.TEN,
                        now.plusDays(7)
                )
        );
        CouponEntity activeValid2 = couponRepository.save(
                CouponEntity.create(
                        "ACTIVE_VALID_2",
                        CouponType.FIXED_AMOUNT,
                        BigDecimal.ONE,
                        now.plusDays(1)
                )
        );

        // 비활성 쿠폰, 만료된 쿠폰 생성
        CouponEntity inactiveCoupon = couponRepository.save(
                CouponEntity.create(
                        "INACTIVE_COUPON",
                        CouponType.FIXED_AMOUNT,
                        BigDecimal.ONE,
                        now.plusDays(3)
                )
        );
        inactiveCoupon.delete();
        couponRepository.save(inactiveCoupon);

        CouponEntity expiredCoupon = couponRepository.save(
                CouponEntity.create(
                        "EXPIRED_COUPON",
                        CouponType.FIXED_AMOUNT,
                        BigDecimal.ONE,
                        now.minusDays(1)
                )
        );

        // 다운로드 상태 소유 쿠폰 2개, 사용 상태 소유 쿠폰 1개, 다른 유저의 소유 쿠폰 1개 생성
        OwnedCouponEntity ownedCoupon1 = ownedCouponRepository.save(
                OwnedCouponEntity.create(
                        userId,
                        activeValid1.getId(),
                        OwnedCouponState.DOWNLOADED
                )
        );
        OwnedCouponEntity ownedCoupon2 = ownedCouponRepository.save(
                OwnedCouponEntity.create(
                        userId,
                        activeValid2.getId(),
                        OwnedCouponState.DOWNLOADED
                )
        );
        ownedCouponRepository.save(
                OwnedCouponEntity.create(
                        userId,
                        inactiveCoupon.getId(),
                        OwnedCouponState.USED
                )
        );
        ownedCouponRepository.save(
                OwnedCouponEntity.create(
                        userId,
                        expiredCoupon.getId(),
                        OwnedCouponState.DOWNLOADED
                )
        );
        ownedCouponRepository.save(
                OwnedCouponEntity.create(
                        200L,
                        activeValid1.getId(),
                        OwnedCouponState.DOWNLOADED
                )
        );

        // when
        List<OwnedCouponEntity> result = ownedCouponRepository.findOwnedCouponIds(
                userId,
                Arrays.asList(activeValid1.getId(), activeValid2.getId(), inactiveCoupon.getId(), expiredCoupon.getId()),
                now
        );

        // then
        Set<Long> resultIds = result.stream().map(OwnedCouponEntity::getId).collect(Collectors.toSet());
        assertThat(resultIds).containsExactlyInAnyOrder(ownedCoupon1.getId(), ownedCoupon2.getId());
    }
}
