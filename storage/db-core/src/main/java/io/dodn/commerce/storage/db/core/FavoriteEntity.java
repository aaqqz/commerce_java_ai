package io.dodn.commerce.storage.db.core;

import jakarta.persistence.Entity;
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
    private Long productId;
    private LocalDateTime favoritedAt;

    public static FavoriteEntity create(Long userId, Long productId, LocalDateTime favoritedAt) {
        FavoriteEntity favorite = new FavoriteEntity();
        favorite.userId = userId;
        favorite.productId = productId;
        favorite.favoritedAt = favoritedAt;

        return favorite;
    }

    public void favorite() {
        active();
        this.favoritedAt = LocalDateTime.now();
    }
}
