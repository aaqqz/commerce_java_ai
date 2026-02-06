package io.dodn.commerce.core.api.controller.v1;

import io.dodn.commerce.core.api.controller.v1.request.ApplyFavoriteRequest;
import io.dodn.commerce.core.api.controller.v1.response.FavoriteResponse;
import io.dodn.commerce.core.api.facade.FavoriteFacade;
import io.dodn.commerce.core.domain.FavoriteService;
import io.dodn.commerce.core.domain.User;
import io.dodn.commerce.core.support.OffsetLimit;
import io.dodn.commerce.core.support.response.ApiResponse;
import io.dodn.commerce.core.support.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class FavoriteController {
    private final FavoriteService favoriteService;
    private final FavoriteFacade favoriteFacade;

    @GetMapping("/v1/favorites")
    public ApiResponse<PageResponse<FavoriteResponse>> getFavorites(
            User user,
            @RequestParam Integer offset,
            @RequestParam Integer limit
    ) {
        var page = favoriteService.findFavorites(user, OffsetLimit.of(offset, limit));
        return ApiResponse.success(PageResponse.of(FavoriteResponse.of(page.content()), page.hasNext()));
    }

    @PostMapping("/v1/favorites")
    public ApiResponse<Object> applyFavorite(User user, @RequestBody ApplyFavoriteRequest request) {
        favoriteFacade.applyFavorite(user, request);
        return ApiResponse.success();
    }
}

