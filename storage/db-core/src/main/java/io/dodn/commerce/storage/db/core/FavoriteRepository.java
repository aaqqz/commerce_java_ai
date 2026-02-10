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
    Optional<FavoriteEntity> findByUserIdAndTargetTypeAndTargetId(Long userId, FavoriteTargetType targetType, Long targetId);
    Slice<FavoriteEntity> findByUserIdAndTargetTypeAndStatusAndUpdatedAtAfter(Long id, FavoriteTargetType targetType, EntityStatus status, LocalDateTime updatedAtAfter, Pageable pageable);

    @Query(
        """
        SELECT f.targetId as targetId, COUNT(f) as count
        FROM FavoriteEntity f
        WHERE f.targetType = :targetType AND f.targetId IN :productIds
            AND f.status = :status
            AND f.favoritedAt >= :from
        GROUP BY f.targetId
        """
    )
    List<TargetCountProjection> countByProductIdsAndStatusAndFavoritedAtAfter(
            List<Long> productIds,
            FavoriteTargetType targetType,
            EntityStatus status,
            LocalDateTime from
    );
}
