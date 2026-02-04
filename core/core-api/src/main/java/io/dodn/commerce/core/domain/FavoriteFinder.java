package io.dodn.commerce.core.domain;

import io.dodn.commerce.core.enums.EntityStatus;
import io.dodn.commerce.storage.db.core.FavoriteRepository;
import io.dodn.commerce.storage.db.core.TargetCountProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FavoriteFinder {
    private final FavoriteRepository favoriteRepository;

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
