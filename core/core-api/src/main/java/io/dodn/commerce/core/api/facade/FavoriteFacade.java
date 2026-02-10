package io.dodn.commerce.core.api.facade;

import io.dodn.commerce.core.api.controller.v1.request.ApplyFavoriteRequest;
import io.dodn.commerce.core.api.controller.v1.response.FavoriteResponse;
import io.dodn.commerce.core.domain.*;
import io.dodn.commerce.core.enums.FavoriteTargetType;
import io.dodn.commerce.core.support.OffsetLimit;
import io.dodn.commerce.core.support.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FavoriteFacade {
    private final FavoriteService favoriteService;
    private final ProductService productService;
    private final BrandService brandService;
    private final MerchantService merchantService;

    public Page<FavoriteResponse> getFavorites(User user, FavoriteTargetType targetType, OffsetLimit offsetLimit) {
        return switch (targetType) {
            case PRODUCT -> getProductFavorites(user, targetType, offsetLimit);
            case BRAND -> getBrandFavorites(user, targetType, offsetLimit);
            case MERCHANT -> getMerchantFavorites(user, targetType, offsetLimit);
        };
    }

    private Page<FavoriteResponse> getProductFavorites(User user, FavoriteTargetType targetType, OffsetLimit offsetLimit) {
        var favorites = favoriteService.findFavorites(user, targetType, offsetLimit);
        var productIds = favorites.content().stream()
                .filter(it -> it.targetType().equals(FavoriteTargetType.PRODUCT))
                .map(Favorite::targetId)
                .distinct()
                .toList();

        Map<Long, Product> productMap = productService.findProducts(productIds).stream()
                .collect(Collectors.toMap(Product::id, p -> p));

        return new Page<>(FavoriteResponse.ofProducts(favorites.content(), productMap), favorites.hasNext());
    }

    private Page<FavoriteResponse> getBrandFavorites(User user, FavoriteTargetType targetType, OffsetLimit offsetLimit) {
        var favorites = favoriteService.findFavorites(user, targetType, offsetLimit);
        var brandIds = favorites.content().stream()
                .filter(it -> it.targetType().equals(FavoriteTargetType.BRAND))
                .map(Favorite::targetId)
                .distinct()
                .toList();

        Map<Long, Brand> brandMap = brandService.find(brandIds).stream()
                .collect(Collectors.toMap(Brand::id, b -> b));

        return new Page<>(FavoriteResponse.ofBrands(favorites.content(), brandMap), favorites.hasNext());
    }

    private Page<FavoriteResponse> getMerchantFavorites(User user, FavoriteTargetType targetType, OffsetLimit offsetLimit) {
        var favorites = favoriteService.findFavorites(user, targetType, offsetLimit);
        var merchantIds = favorites.content().stream()
                .filter(it -> it.targetType().equals(FavoriteTargetType.MERCHANT))
                .map(Favorite::targetId)
                .distinct()
                .toList();

        Map<Long, Merchant> merchantMap = merchantService.find(merchantIds).stream()
                .collect(Collectors.toMap(Merchant::id, m -> m));

        return new Page<>(FavoriteResponse.ofMerchants(favorites.content(), merchantMap), favorites.hasNext());
    }

    public void applyFavorite(User user, ApplyFavoriteRequest request) {
        // 호환 처리: 기존 클라이언트는 productId 만 보낼 수 있음
        FavoriteTargetType targetType = request.resolveTargetType();
        Long targetId = request.resolveTargetId();

        switch (request.type()) {
            case FAVORITE -> favoriteService.addFavorite(user, targetType, targetId);
            case UNFAVORITE -> favoriteService.removeFavorite(user, targetType, targetId);
        }
    }
}
