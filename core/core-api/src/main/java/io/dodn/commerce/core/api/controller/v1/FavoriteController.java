package io.dodn.commerce.core.api.controller.v1;

import io.dodn.commerce.core.api.controller.v1.request.ApplyFavoriteRequest;
import io.dodn.commerce.core.api.controller.v1.request.ApplyFavoriteRequestType;
import io.dodn.commerce.core.api.controller.v1.response.FavoriteResponse;
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

    @GetMapping("/v1/favorites")
    public ApiResponse<PageResponse<FavoriteResponse>> getFavorites(
            User user,
            @RequestParam Integer offset,
            @RequestParam Integer limit) {
        var page = favoriteService.findFavorites(user, new OffsetLimit(offset, limit));
        return ApiResponse.success(new PageResponse<>(FavoriteResponse.of(page.getContent()), page.isHasNext()));
    }

    @PostMapping("/v1/favorites")
    public ApiResponse<Object> applyFavorite(
            User user,
            @RequestBody ApplyFavoriteRequest request) {
        if (request.getType() == ApplyFavoriteRequestType.FAVORITE) {
            favoriteService.addFavorite(user, request.getProductId());
        } else if (request.getType() == ApplyFavoriteRequestType.UNFAVORITE) {
            favoriteService.removeFavorite(user, request.getProductId());
        }
        return ApiResponse.success();
    }
}
