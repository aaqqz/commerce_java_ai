package io.dodn.commerce.core.domain;

import io.dodn.commerce.core.enums.FavoriteTargetType;
import io.dodn.commerce.core.support.OffsetLimit;
import io.dodn.commerce.core.support.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FavoriteService {
    private final FavoriteFinder favoriteFinder;
    private final FavoriteManager favoriteManager;

    public Page<Favorite> findFavorites(User user, FavoriteTargetType targetType, OffsetLimit offsetLimit) {
        return favoriteFinder.findFavorites(user, targetType, offsetLimit);
    }

    public Long addFavorite(User user, FavoriteTarget target) {
        return favoriteManager.addFavorite(user, target);
    }

    public Long removeFavorite(User user, FavoriteTarget target) {
        return favoriteManager.removeFavorite(user, target);
    }

    public Map<Long, Long> recentCount(FavoriteTargetType targetType, List<Long> targetIds, LocalDateTime from) {
        return favoriteFinder.countByTargets(targetType, targetIds, from);
    }

    @Deprecated
    public Page<Favorite> findFavorites(User user, OffsetLimit offsetLimit) {
        return findFavorites(user, FavoriteTargetType.PRODUCT, offsetLimit);
    }

    @Deprecated
    public Long addFavorite(User user, Long productId) {
        return addFavorite(user, new FavoriteTarget(FavoriteTargetType.PRODUCT, productId));
    }

    @Deprecated
    public Long removeFavorite(User user, Long productId) {
        return removeFavorite(user, new FavoriteTarget(FavoriteTargetType.PRODUCT, productId));
    }

    @Deprecated
    public Map<Long, Long> recentCount(List<Long> productIds, LocalDateTime from) {
        return recentCount(FavoriteTargetType.PRODUCT, productIds, from);
    }
}
