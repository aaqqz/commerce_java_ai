package io.dodn.commerce.core.api.facade;

import io.dodn.commerce.core.api.controller.v1.request.ApplyFavoriteRequest;
import io.dodn.commerce.core.domain.FavoriteService;
import io.dodn.commerce.core.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FavoriteFacade {
    private final FavoriteService favoriteService;

    public void applyFavorite(User user, ApplyFavoriteRequest request) {
        switch (request.type()) {
            case FAVORITE -> favoriteService.addFavorite(user, request.productId());
            case UNFAVORITE -> favoriteService.removeFavorite(user, request.productId());
        }
    }
}
