package io.dodn.commerce.core.api.facade;

import io.dodn.commerce.core.api.controller.v1.request.ApplyFavoriteRequest;
import io.dodn.commerce.core.api.controller.v1.response.FavoriteResponse;
import io.dodn.commerce.core.domain.*;
import io.dodn.commerce.core.support.OffsetLimit;
import io.dodn.commerce.core.support.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FavoriteFacade {
    private final FavoriteService favoriteService;
    private final ProductService productService;

    public void applyFavorite(User user, ApplyFavoriteRequest request) {
        switch (request.type()) {
            case FAVORITE -> favoriteService.addFavorite(user, request.productId());
            case UNFAVORITE -> favoriteService.removeFavorite(user, request.productId());
        }
    }

    public Page<FavoriteResponse> getFavorites(User user, OffsetLimit offsetLimit) {
        // 1. 찜 목록 조회 (기존 FavoriteService 메서드 활용)
        var favoritePage = favoriteService.findFavorites(user, offsetLimit);
        List<Favorite> favorites = favoritePage.content();

        // 2. productId 목록 추출
        List<Long> productIds = favorites.stream()
                .map(Favorite::productId)
                .toList();

        // 3. 상품 정보 일괄 조회
        List<Product> products = productService.findProducts(productIds);

        // 4. productId를 키로 하는 Map 생성
        Map<Long, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::id, Function.identity()));

        // 5. FavoriteResponse 리스트 생성 (Map으로 병합)
        List<FavoriteResponse> responses = favorites.stream()
                .map(favorite -> FavoriteResponse.of(favorite, productMap.get(favorite.productId())))
                .toList();

        return new Page<>(responses, favoritePage.hasNext());
    }
}
