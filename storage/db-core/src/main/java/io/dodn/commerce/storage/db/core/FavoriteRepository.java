package io.dodn.commerce.storage.db.core;

import io.dodn.commerce.core.enums.EntityStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<FavoriteEntity, Long> {
    Optional<FavoriteEntity> findByUserIdAndProductId(Long userId, Long productId);
    Slice<FavoriteEntity> findByUserIdAndStatusAndUpdatedAtAfter(Long userId, EntityStatus status, LocalDateTime updatedAtAfter, Pageable pageable);
}
