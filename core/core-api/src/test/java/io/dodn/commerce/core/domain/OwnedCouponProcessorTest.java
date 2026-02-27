package io.dodn.commerce.core.domain;

import io.dodn.commerce.ContextTest;
import io.dodn.commerce.core.enums.OwnedCouponState;
import io.dodn.commerce.core.support.error.CoreException;
import io.dodn.commerce.core.support.error.ErrorType;
import io.dodn.commerce.storage.db.core.OwnedCouponEntity;
import io.dodn.commerce.storage.db.core.OwnedCouponRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class OwnedCouponProcessorTest extends ContextTest {

    private final OwnedCouponProcessor ownedCouponProcessor;
    private final OwnedCouponRepository ownedCouponRepository;

    @Autowired
    public OwnedCouponProcessorTest(
            OwnedCouponProcessor ownedCouponProcessor,
            OwnedCouponRepository ownedCouponRepository
    ) {
        this.ownedCouponProcessor = ownedCouponProcessor;
        this.ownedCouponRepository = ownedCouponRepository;
    }

    @Test
    @Transactional
    void 사용_1회_후_remainingUses가_감소한다() {
        // given
        OwnedCouponEntity entity = ownedCouponRepository.save(
                OwnedCouponEntity.create(1L, 1L, OwnedCouponState.DOWNLOADED, 3)
        );

        // when
        ownedCouponProcessor.useOne(entity.getId());

        // then
        OwnedCouponEntity result = ownedCouponRepository.findById(entity.getId()).orElseThrow();
        assertThat(result.getUsedCount()).isEqualTo(1);
        assertThat(result.remainingUses()).isEqualTo(2);
        assertThat(result.getState()).isEqualTo(OwnedCouponState.DOWNLOADED);
    }

    @Test
    @Transactional
    void 마지막_사용_시_EXHAUSTED로_전이된다() {
        // given
        OwnedCouponEntity entity = ownedCouponRepository.save(
                OwnedCouponEntity.create(1L, 1L, OwnedCouponState.DOWNLOADED, 2)
        );

        // when
        ownedCouponProcessor.useOne(entity.getId());
        ownedCouponProcessor.useOne(entity.getId());

        // then
        OwnedCouponEntity result = ownedCouponRepository.findById(entity.getId()).orElseThrow();
        assertThat(result.getState()).isEqualTo(OwnedCouponState.EXHAUSTED);
        assertThat(result.remainingUses()).isEqualTo(0);
    }

    @Test
    @Transactional
    void 잔여_사용_횟수가_없을_때_useOne_예외() {
        // given: totalUses=1로 저장 후 processor를 통해 EXHAUSTED 상태로 전이
        OwnedCouponEntity entity = ownedCouponRepository.save(
                OwnedCouponEntity.create(1L, 1L, OwnedCouponState.DOWNLOADED, 1)
        );
        ownedCouponProcessor.useOne(entity.getId()); // 1회 사용 → EXHAUSTED

        // when & then: 한 번 더 사용 시 예외 발생
        assertThatThrownBy(() -> ownedCouponProcessor.useOne(entity.getId()))
                .isInstanceOf(CoreException.class)
                .satisfies(e -> assertThat(((CoreException) e).getErrorType())
                        .isEqualTo(ErrorType.OWNED_COUPON_NO_REMAINING_USES));
    }

    @Test
    @Transactional
    void revertOne_EXHAUSTED에서_DOWNLOADED로_복구된다() {
        // given: totalUses=1로 저장 후 processor를 통해 EXHAUSTED 상태로 전이
        OwnedCouponEntity entity = ownedCouponRepository.save(
                OwnedCouponEntity.create(1L, 1L, OwnedCouponState.DOWNLOADED, 1)
        );
        ownedCouponProcessor.useOne(entity.getId()); // EXHAUSTED 상태로 전이

        // when
        ownedCouponProcessor.revertOne(entity.getId());

        // then
        OwnedCouponEntity result = ownedCouponRepository.findById(entity.getId()).orElseThrow();
        assertThat(result.getState()).isEqualTo(OwnedCouponState.DOWNLOADED);
        assertThat(result.remainingUses()).isEqualTo(1);
    }
}
