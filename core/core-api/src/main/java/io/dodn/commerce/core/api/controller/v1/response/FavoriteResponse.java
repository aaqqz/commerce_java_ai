package io.dodn.commerce.core.api.controller.v1.response;

import io.dodn.commerce.core.domain.Favorite;
import java.time.LocalDateTime;
import java.util.List;

public record FavoriteResponse(
        Long id,
        Long productId,
        LocalDateTime favoritedAt
) {
    public static FavoriteResponse of(Favorite favorite) {
        return new FavoriteResponse(
                favorite.id(),
                favorite.productId(),
                favorite.favoritedAt()
        );
    }

    public static List<FavoriteResponse> of(List<Favorite> favorites) {
        return favorites.stream().map(FavoriteResponse::of).toList();
    }
}
