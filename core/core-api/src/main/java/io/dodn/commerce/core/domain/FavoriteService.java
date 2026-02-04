package io.dodn.commerce.core.domain;

import io.dodn.commerce.core.enums.EntityStatus;
import io.dodn.commerce.core.support.OffsetLimit;
import io.dodn.commerce.core.support.Page;
import io.dodn.commerce.core.support.error.CoreException;
import io.dodn.commerce.core.support.error.ErrorType;
import io.dodn.commerce.storage.db.core.FavoriteEntity;
import io.dodn.commerce.storage.db.core.FavoriteRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FavoriteService {
    private final FavoriteRepository favoriteRepository;
    private final FavoriteFinder favoriteFinder;

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

    @Transactional
    public Long addFavorite(User user, Long productId) {
        FavoriteEntity found = favoriteRepository.findByUserIdAndProductId(user.id(), productId)
                .map(entity -> {
                    entity.active();
                    return entity;
                })
                .orElseGet(() -> favoriteRepository.save(
                        FavoriteEntity.create(
                                user.id(),
                                productId,
                                LocalDateTime.now()
                        )
                ));

        return found.getId();
    }

    @Transactional
    public Long removeFavorite(User user, Long productId) {
        FavoriteEntity existing = favoriteRepository.findByUserIdAndProductId(user.id(), productId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND_DATA));

        existing.delete();
        return existing.getId();
    }

    public Map<Long, Long> recentCount(List<Long> productIds, LocalDateTime from) {
        return favoriteFinder.countByProductIds(productIds, from);
    }
}
