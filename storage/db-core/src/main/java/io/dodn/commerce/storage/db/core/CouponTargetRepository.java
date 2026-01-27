package io.dodn.commerce.storage.db.core;

import io.dodn.commerce.core.enums.CouponTargetType;
import io.dodn.commerce.core.enums.EntityStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface CouponTargetRepository extends JpaRepository<CouponTargetEntity, Long> {
    List<CouponTargetEntity> findByTargetTypeAndTargetIdInAndStatus(CouponTargetType target, Collection<Long> targetId, EntityStatus status);
}
