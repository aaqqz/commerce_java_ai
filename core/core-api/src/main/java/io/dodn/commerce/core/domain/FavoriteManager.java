package io.dodn.commerce.core.domain;

import io.dodn.commerce.core.enums.FavoriteTargetType;
import io.dodn.commerce.core.support.error.CoreException;
import io.dodn.commerce.core.support.error.ErrorType;
import io.dodn.commerce.storage.db.core.FavoriteEntity;
import io.dodn.commerce.storage.db.core.FavoriteRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class FavoriteManager {
    private final FavoriteRepository favoriteRepository;

    @Transactional
    public Long addFavorite(User user, FavoriteTarget target) {
        FavoriteEntity found = favoriteRepository.findByUserIdAndTargetTypeAndTargetId(user.id(), target.type(), target.id())
                .map(entity -> {
                    entity.favorite();
                    return entity;
                })
                .orElseGet(() -> favoriteRepository.save(
                        FavoriteEntity.create(user.id(), target.type(), target.id(), LocalDateTime.now())
                ));
        return found.getId();
    }

    @Transactional
    public Long removeFavorite(User user, FavoriteTarget target) {
        FavoriteEntity existing = favoriteRepository.findByUserIdAndTargetTypeAndTargetId(user.id(), target.type(), target.id())
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND_DATA));
        existing.delete();
        return existing.getId();
    }

    @Deprecated
    @Transactional
    public Long addFavorite(User user, Long productId) {
        return addFavorite(user, new FavoriteTarget(FavoriteTargetType.PRODUCT, productId));
    }

    @Deprecated
    @Transactional
    public Long removeFavorite(User user, Long productId) {
        return removeFavorite(user, new FavoriteTarget(FavoriteTargetType.PRODUCT, productId));
    }
}
