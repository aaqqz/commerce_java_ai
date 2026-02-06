package io.dodn.commerce.core.domain;

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

    public Page<Favorite> findFavorites(User user, OffsetLimit offsetLimit) {
        return favoriteFinder.findFavorites(user, offsetLimit);
    }

    public Long addFavorite(User user, Long productId) {
        return favoriteManager.addFavorite(user, productId);
    }

    public Long removeFavorite(User user, Long productId) {
        return favoriteManager.removeFavorite(user, productId);
    }

    public Map<Long, Long> recentCount(List<Long> productIds, LocalDateTime from) {
        return favoriteFinder.countByProductIds(productIds, from);
    }
}
