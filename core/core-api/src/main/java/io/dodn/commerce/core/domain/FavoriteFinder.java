package io.dodn.commerce.core.domain;

import io.dodn.commerce.core.enums.EntityStatus;
import io.dodn.commerce.core.enums.FavoriteTargetType;
import io.dodn.commerce.core.support.OffsetLimit;
import io.dodn.commerce.core.support.Page;
import io.dodn.commerce.storage.db.core.FavoriteEntity;
import io.dodn.commerce.storage.db.core.FavoriteRepository;
import io.dodn.commerce.storage.db.core.TargetCountProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FavoriteFinder {
    private final FavoriteRepository favoriteRepository;

    public Page<Favorite> findFavorites(User user, FavoriteTargetType targetType, OffsetLimit offsetLimit) {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(30);
        Slice<FavoriteEntity> result = favoriteRepository.findByUserIdAndTargetTypeAndStatusAndUpdatedAtAfter(
                user.id(),
                targetType,
                EntityStatus.ACTIVE,
                cutoff,
                offsetLimit.toPageable()
        );

        return new Page<>(
                result.getContent().stream()
                        .map(it -> new Favorite(
                                it.getId(),
                                it.getUserId(),
                                new FavoriteTarget(it.getTargetType(), it.getTargetId()),
                                it.getFavoritedAt()
                        ))
                        .toList(),
                result.hasNext()
        );
    }

    @Deprecated
    public Page<Favorite> findFavorites(User user, OffsetLimit offsetLimit) {
        return findFavorites(user, FavoriteTargetType.PRODUCT, offsetLimit);
    }

    public Map<Long, Long> countByTargets(FavoriteTargetType targetType, List<Long> targetIds, LocalDateTime from) {
        if (targetIds.isEmpty()) {
            return Map.of();
        }

        List<TargetCountProjection> results = favoriteRepository.countByTargetTypeAndTargetIdsAndStatusAndFavoritedAtAfter(
                targetType,
                targetIds,
                EntityStatus.ACTIVE,
                from
        );

        return results.stream()
                .collect(Collectors.toMap(
                        TargetCountProjection::getTargetId,
                        TargetCountProjection::getCount
                ));
    }

    @Deprecated
    public Map<Long, Long> countByProductIds(List<Long> productIds, LocalDateTime from) {
        return countByTargets(FavoriteTargetType.PRODUCT, productIds, from);
    }
}
