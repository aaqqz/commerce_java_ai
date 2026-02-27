package io.dodn.commerce.storage.db.core;

import io.dodn.commerce.core.enums.OwnedCouponState;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OwnedCouponEntityTest {

    @Test
    void useOne_호출_시_usedCount가_증가하고_remainingUses가_감소한다() {
        OwnedCouponEntity entity = OwnedCouponEntity.create(1L, 1L, OwnedCouponState.DOWNLOADED, 3);

        entity.useOne();

        assertThat(entity.getUsedCount()).isEqualTo(1);
        assertThat(entity.remainingUses()).isEqualTo(2);
        assertThat(entity.getState()).isEqualTo(OwnedCouponState.DOWNLOADED);
    }

    @Test
    void useOne_마지막_사용_시_EXHAUSTED로_전이된다() {
        OwnedCouponEntity entity = OwnedCouponEntity.create(1L, 1L, OwnedCouponState.DOWNLOADED, 2);

        entity.useOne();
        entity.useOne();

        assertThat(entity.getUsedCount()).isEqualTo(2);
        assertThat(entity.remainingUses()).isEqualTo(0);
        assertThat(entity.getState()).isEqualTo(OwnedCouponState.EXHAUSTED);
    }

    @Test
    void revertOne_EXHAUSTED_상태에서_DOWNLOADED로_복구된다() {
        OwnedCouponEntity entity = OwnedCouponEntity.create(1L, 1L, OwnedCouponState.DOWNLOADED, 1);
        entity.useOne(); // EXHAUSTED 상태로 전이

        entity.revertOne();

        assertThat(entity.getUsedCount()).isEqualTo(0);
        assertThat(entity.remainingUses()).isEqualTo(1);
        assertThat(entity.getState()).isEqualTo(OwnedCouponState.DOWNLOADED);
    }

    @Test
    void revertOne_usedCount가_0이면_감소하지_않는다() {
        OwnedCouponEntity entity = OwnedCouponEntity.create(1L, 1L, OwnedCouponState.DOWNLOADED, 3);

        entity.revertOne();

        assertThat(entity.getUsedCount()).isEqualTo(0);
        assertThat(entity.remainingUses()).isEqualTo(3);
    }

    @Test
    void totalUses_1로_생성_시_첫_사용_후_EXHAUSTED_전이된다() {
        OwnedCouponEntity entity = OwnedCouponEntity.create(1L, 1L, OwnedCouponState.DOWNLOADED, 1);

        entity.useOne();

        assertThat(entity.getState()).isEqualTo(OwnedCouponState.EXHAUSTED);
        assertThat(entity.remainingUses()).isEqualTo(0);
    }
}
