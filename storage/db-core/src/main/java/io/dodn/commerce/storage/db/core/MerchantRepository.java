package io.dodn.commerce.storage.db.core;

import io.dodn.commerce.core.enums.EntityStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MerchantRepository extends JpaRepository<MerchantEntity, Long> {
    List<MerchantEntity> findByIdInAndStatus(List<Long> ids, EntityStatus status);
}
