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

    public Long addFavorite(User user, FavoriteTargetType targetType, Long targetId) {
        return favoriteManager.addFavorite(user, targetType, targetId);
    }

    public Long removeFavorite(User user, FavoriteTargetType targetType, Long targetId) {
        return favoriteManager.removeFavorite(user, targetType, targetId);
    }

    public Map<Long, Long> recentCount(List<Long> productIds, LocalDateTime from) {
        return favoriteFinder.countByProductIds(productIds, from);
    }
}
