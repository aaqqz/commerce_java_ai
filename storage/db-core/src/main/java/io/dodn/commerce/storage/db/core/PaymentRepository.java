package io.dodn.commerce.storage.db.core;

import io.dodn.commerce.core.enums.PaymentState;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {
    PaymentEntity findByOrderId(Long orderId);
    Slice<PaymentEntity> findAllByStateAndPaidAtBetween(PaymentState state, LocalDateTime from, LocalDateTime to, Pageable pageable);
}
