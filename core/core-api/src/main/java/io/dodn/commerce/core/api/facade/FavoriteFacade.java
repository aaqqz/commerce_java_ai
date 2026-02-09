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
        var page = favoriteService.findFavorites(user, offsetLimit);

        // 2. productId 목록 추출
        List<Long> productIds = page.content().stream()
                .map(Favorite::productId)
                .distinct()
                .toList();

        // 3. 상품 정보 일괄 조회 후 productId를 키로 하는 Map 생성
        Map<Long, Product> productMap = productService.findProducts(productIds).stream()
                .collect(Collectors.toMap(Product::id, p -> p));

        // 4. Page<FavoriteResponse> 반환
        return new Page<>(FavoriteResponse.of(page.content(), productMap), page.hasNext());
    }
}
