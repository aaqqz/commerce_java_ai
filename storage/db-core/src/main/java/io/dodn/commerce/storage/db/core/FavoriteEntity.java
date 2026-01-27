package io.dodn.commerce.storage.db.core;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "favorite")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FavoriteEntity extends BaseEntity {
    private Long userId;
    private Long productId;
    private LocalDateTime favoritedAt;

    public FavoriteEntity(Long userId, Long productId, LocalDateTime favoritedAt) {
        this.userId = userId;
        this.productId = productId;
        this.favoritedAt = favoritedAt;
    }

    public void favorite() {
        active();
        this.favoritedAt = LocalDateTime.now();
    }
}
