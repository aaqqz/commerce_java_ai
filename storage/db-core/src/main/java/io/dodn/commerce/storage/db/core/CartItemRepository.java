package io.dodn.commerce.storage.db.core;

import io.dodn.commerce.core.enums.EntityStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItemEntity, Long> {
    List<CartItemEntity> findByUserIdAndStatus(Long userId, EntityStatus status);
    CartItemEntity findByUserIdAndIdAndStatus(Long userId, Long id, EntityStatus status);
    CartItemEntity findByUserIdAndProductId(Long userId, Long productId);
}
