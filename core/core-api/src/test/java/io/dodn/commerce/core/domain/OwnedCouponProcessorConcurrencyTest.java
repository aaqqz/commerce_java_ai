package io.dodn.commerce.core.domain;

import io.dodn.commerce.core.enums.OwnedCouponState;
import io.dodn.commerce.storage.db.core.OwnedCouponEntity;
import io.dodn.commerce.storage.db.core.OwnedCouponRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class OwnedCouponProcessorConcurrencyTest {

    @Autowired
    private OwnedCouponProcessor ownedCouponProcessor;

    @Autowired
    private OwnedCouponRepository ownedCouponRepository;

    private Long savedEntityId;

    @AfterEach
    void cleanup() {
        if (savedEntityId != null) {
            ownedCouponRepository.deleteById(savedEntityId);
            savedEntityId = null;
        }
    }

    @Test
    void 동시_useOne_2회_중_정확히_1회만_성공한다() throws InterruptedException {
        // given: totalUses=1인 쿠폰 생성
        OwnedCouponEntity entity = ownedCouponRepository.save(
                OwnedCouponEntity.create(999L, 999L, OwnedCouponState.DOWNLOADED, 1)
        );
        savedEntityId = entity.getId();

        int threadCount = 2;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // when: 2개 스레드 동시에 useOne 호출
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    ownedCouponProcessor.useOne(entity.getId());
                    successCount.incrementAndGet();
                } catch (ObjectOptimisticLockingFailureException e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        // then: 정확히 1개만 성공, 1개는 낙관적 락 예외
        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failCount.get()).isEqualTo(1);
    }
}
