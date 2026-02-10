package io.dodn.commerce.storage.db.core;

import io.dodn.commerce.core.enums.FavoriteTargetType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "favorite")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FavoriteEntity extends BaseEntity {
    private Long userId;

    @Enumerated(EnumType.STRING)
    private FavoriteTargetType targetType;

    private Long targetId;

    @Deprecated
    private Long productId;

    private LocalDateTime favoritedAt;

    public static FavoriteEntity create(Long userId, FavoriteTargetType targetType, Long targetId, LocalDateTime favoritedAt) {
        FavoriteEntity favorite = new FavoriteEntity();
        favorite.userId = userId;
        favorite.targetType = targetType;
        favorite.targetId = targetId;
        favorite.favoritedAt = favoritedAt;

        return favorite;
    }

    @Deprecated
    public static FavoriteEntity create(Long userId, Long productId, LocalDateTime favoritedAt) {
        FavoriteEntity favorite = new FavoriteEntity();
        favorite.userId = userId;
        favorite.targetType = FavoriteTargetType.PRODUCT;
        favorite.targetId = productId;
        favorite.productId = productId;
        favorite.favoritedAt = favoritedAt;

        return favorite;
    }

    public void favorite() {
        active();
        this.favoritedAt = LocalDateTime.now();
    }
}
