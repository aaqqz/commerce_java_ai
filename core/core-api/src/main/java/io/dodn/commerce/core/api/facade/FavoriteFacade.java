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

    public void applyFavorite(User user, ApplyFavoriteRequest request) {
        FavoriteTarget target = new FavoriteTarget(request.getTargetType(), request.targetId());

        switch (request.type()) {
            case FAVORITE -> favoriteService.addFavorite(user, target);
            case UNFAVORITE -> favoriteService.removeFavorite(user, target);
        }
    }

    public Page<FavoriteResponse> getFavorites(User user, FavoriteTargetType targetType, OffsetLimit offsetLimit) {
        var page = favoriteService.findFavorites(user, targetType, offsetLimit);

        List<Long> targetIds = page.content().stream()
                .map(favorite -> favorite.target().id())
                .distinct()
                .toList();

        List<FavoriteResponse> responses = switch (targetType) {
            case PRODUCT -> {
                Map<Long, Product> productMap = productService.findProducts(targetIds).stream()
                        .collect(Collectors.toMap(Product::id, p -> p));
                yield FavoriteResponse.ofProducts(page.content(), productMap);
            }
            case BRAND -> {
                Map<Long, Brand> brandMap = brandService.find(targetIds).stream()
                        .collect(Collectors.toMap(Brand::id, b -> b));
                yield FavoriteResponse.ofBrands(page.content(), brandMap);
            }
            case MERCHANT -> {
                Map<Long, Merchant> merchantMap = merchantService.find(targetIds).stream()
                        .collect(Collectors.toMap(Merchant::id, m -> m));
                yield FavoriteResponse.ofMerchants(page.content(), merchantMap);
            }
        };

        return new Page<>(responses, page.hasNext());
    }
}
