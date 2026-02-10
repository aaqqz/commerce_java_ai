package io.dodn.commerce.storage.db.core;

import io.dodn.commerce.core.enums.EntityStatus;
import io.dodn.commerce.core.enums.FavoriteTargetType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<FavoriteEntity, Long> {
    Optional<FavoriteEntity> findByUserIdAndProductId(Long userId, Long productId);

    Optional<FavoriteEntity> findByUserIdAndTargetTypeAndTargetId(Long userId, FavoriteTargetType targetType, Long targetId);

    Slice<FavoriteEntity> findByUserIdAndStatusAndUpdatedAtAfter(Long userId, EntityStatus status, LocalDateTime updatedAtAfter, Pageable pageable);

    Slice<FavoriteEntity> findByUserIdAndTargetTypeAndStatusAndUpdatedAtAfter(Long userId, FavoriteTargetType targetType, EntityStatus status, LocalDateTime updatedAtAfter, Pageable pageable);

    @Query("""
        SELECT f.productId as targetId, COUNT(f.id) as count
        FROM FavoriteEntity f
        WHERE f.productId IN :productIds
            AND f.status = :status
            AND f.favoritedAt >= :from
        GROUP BY f.productId
        """)
    List<TargetCountProjection> countByProductIdsAndStatusAndFavoritedAtAfter(
            List<Long> productIds,
            EntityStatus status,
            LocalDateTime from
    );

    @Query("""
        SELECT f.targetId as targetId, COUNT(f.id) as count
        FROM FavoriteEntity f
        WHERE f.targetType = :targetType
            AND f.targetId IN :targetIds
            AND f.status = :status
            AND f.favoritedAt >= :from
        GROUP BY f.targetId
        """)
    List<TargetCountProjection> countByTargetTypeAndTargetIdsAndStatusAndFavoritedAtAfter(
            FavoriteTargetType targetType,
            List<Long> targetIds,
            EntityStatus status,
            LocalDateTime from
    );
}
