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
    public Long addFavorite(User user, FavoriteTargetType targetType, Long targetId) {
        FavoriteEntity found = favoriteRepository.findByUserIdAndTargetTypeAndTargetId(user.id(), targetType, targetId)
                .map(entity -> {
                    entity.favorite();
                    return entity;
                })
                .orElseGet(() -> favoriteRepository.save(
                        FavoriteEntity.create(user.id(), targetType, targetId, LocalDateTime.now())
                ));
        return found.getId();
    }

    @Transactional
    public Long removeFavorite(User user, FavoriteTargetType targetType, Long targetId) {
        FavoriteEntity existing = favoriteRepository.findByUserIdAndTargetTypeAndTargetId(user.id(), targetType, targetId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND_DATA));
        existing.delete();
        return existing.getId();
    }
}
