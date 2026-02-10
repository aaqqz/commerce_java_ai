package io.dodn.commerce.core.api.controller.v1.response;

import io.dodn.commerce.core.domain.Brand;
import io.dodn.commerce.core.domain.Favorite;
import io.dodn.commerce.core.domain.Merchant;
import io.dodn.commerce.core.domain.Product;
import io.dodn.commerce.core.enums.FavoriteTargetType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public record FavoriteResponse(
        Long id,
        FavoriteTargetType targetType,
        Long targetId,
        String name,
        String imageUrl,
        String description,
        BigDecimal costPrice,
        BigDecimal salesPrice,
        BigDecimal discountedPrice,
        LocalDateTime favoritedAt
) {
    public static List<FavoriteResponse> ofProducts(List<Favorite> favorites, Map<Long, Product> productMap) {
        return favorites.stream()
                .map(it -> {
                    var product = Optional.ofNullable(productMap.get(it.target().id()))
                            .orElseThrow(() -> new IllegalStateException("Product not found. id=" + it.target().id()));

                    return new FavoriteResponse(
                            it.id(),
                            FavoriteTargetType.PRODUCT,
                            it.target().id(),
                            product.name(),
                            product.thumbnailUrl(),
                            product.shortDescription(),
                            product.price().costPrice(),
                            product.price().salesPrice(),
                            product.price().discountedPrice(),
                            it.favoritedAt()
                    );
                })
                .toList();
    }

    public static List<FavoriteResponse> ofBrands(List<Favorite> favorites, Map<Long, Brand> brandMap) {
        return favorites.stream()
                .map(it -> {
                    var brand = Optional.ofNullable(brandMap.get(it.target().id()))
                            .orElseThrow(() -> new IllegalStateException("Brand not found. id=" + it.target().id()));

                    return new FavoriteResponse(
                            it.id(),
                            FavoriteTargetType.BRAND,
                            it.target().id(),
                            brand.name(),
                            brand.logoUrl(),
                            brand.description(),
                            null,
                            null,
                            null,
                            it.favoritedAt()
                    );
                })
                .toList();
    }

    public static List<FavoriteResponse> ofMerchants(List<Favorite> favorites, Map<Long, Merchant> merchantMap) {
        return favorites.stream()
                .map(it -> {
                    var merchant = Optional.ofNullable(merchantMap.get(it.target().id()))
                            .orElseThrow(() -> new IllegalStateException("Merchant not found. id=" + it.target().id()));

                    return new FavoriteResponse(
                            it.id(),
                            FavoriteTargetType.MERCHANT,
                            it.target().id(),
                            merchant.name(),
                            merchant.logoUrl(),
                            merchant.description(),
                            null,
                            null,
                            null,
                            it.favoritedAt()
                    );
                })
                .toList();
    }
}
