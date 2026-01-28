package io.dodn.commerce.storage.db.core;

import io.dodn.commerce.core.enums.EntityStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItemEntity, Long> {
    List<CartItemEntity> findByUserIdAndStatus(Long userId, EntityStatus status);
    Optional<CartItemEntity> findByUserIdAndIdAndStatus(Long userId, Long id, EntityStatus status);
    Optional<CartItemEntity> findByUserIdAndProductId(Long userId, Long productId);
}
