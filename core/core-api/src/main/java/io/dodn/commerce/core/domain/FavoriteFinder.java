package io.dodn.commerce.core.domain;

import io.dodn.commerce.core.enums.EntityStatus;
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

    public Page<Favorite> findFavorites(User user, OffsetLimit offsetLimit) {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(30);
        Slice<FavoriteEntity> result = favoriteRepository.findByUserIdAndStatusAndUpdatedAtAfter(
                user.id(),
                EntityStatus.ACTIVE,
                cutoff,
                offsetLimit.toPageable()
        );

        return new Page<>(
                result.getContent().stream()
                        .map(it -> new Favorite(
                                it.getId(),
                                it.getUserId(),
                                it.getProductId(),
                                it.getFavoritedAt()
                        ))
                        .toList(),
                result.hasNext()
        );
    }

    public Map<Long, Long> countByProductIds(List<Long> productIds, LocalDateTime from) {
        if (productIds.isEmpty()) {
            return Map.of();
        }

        List<TargetCountProjection> results = favoriteRepository.countByProductIdsAndStatusAndFavoritedAtAfter(
                productIds,
                EntityStatus.ACTIVE,
                from
        );

        return results.stream()
                .collect(Collectors.toMap(
                        TargetCountProjection::getProductId,
                        TargetCountProjection::getCount
                ));
    }
}
