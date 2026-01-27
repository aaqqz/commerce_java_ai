package io.dodn.commerce.storage.db.core;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface CancelRepository extends JpaRepository<CancelEntity, Long> {
    boolean existsByPaymentId(Long paymentId);
    List<CancelEntity> findAllByCanceledAtBetween(LocalDateTime from, LocalDateTime to);
    Slice<CancelEntity> findAllByCanceledAtBetween(LocalDateTime from, LocalDateTime to, Pageable pageable);
}
