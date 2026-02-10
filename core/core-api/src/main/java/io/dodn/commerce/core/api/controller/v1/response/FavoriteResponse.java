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
        String targetName,
        String imageUrl,
        String productName, // 하위 호환을 위해 필드 유지
        String productImageUrl, // 하위 호환을 위해 필드 유지
        BigDecimal costPrice,
        BigDecimal salesPrice,
        BigDecimal discountedPrice,
        LocalDateTime favoritedAt,
        Boolean isChanged
) {
    public static List<FavoriteResponse> ofProducts(List<Favorite> favorites, Map<Long, Product> productMap) {
        return favorites.stream()
                .map(it -> {
                    var product = Optional.ofNullable(productMap.get(it.targetId()))
                            .orElseThrow(() -> new IllegalStateException("Product not found. id=" + it.targetId()));

                    return new FavoriteResponse(
                            it.id(),
                            FavoriteTargetType.PRODUCT,
                            product.id(),
                            product.name(),
                            product.thumbnailUrl(),
                            product.name(),
                            product.thumbnailUrl(),
                            product.price().costPrice(),
                            product.price().salesPrice(),
                            product.price().discountedPrice(),
                            it.favoritedAt(),
                            product.updatedAt().isAfter(it.favoritedAt())
                    );
                })
                .toList();
    }


    public static List<FavoriteResponse> ofBrands(List<Favorite> favorites, Map<Long, Brand> brandMap) {
        return favorites.stream()
                .map(it -> {
                    var brand = Optional.ofNullable(brandMap.get(it.targetId()))
                            .orElseThrow(() -> new IllegalStateException("Brand not found. id=" + it.targetId()));

                    return new FavoriteResponse(
                            it.id(),
                            FavoriteTargetType.BRAND,
                            brand.id(),
                            brand.name(),
                            null, null, null, null, null, null,
                            it.favoritedAt(),
                            null
                    );
                })
                .toList();
    }

    public static List<FavoriteResponse> ofMerchants(List<Favorite> favorites, Map<Long, Merchant> merchantMap) {
        return favorites.stream()
                .map(it -> {
                    var merchant = Optional.ofNullable(merchantMap.get(it.targetId()))
                            .orElseThrow(() -> new IllegalStateException("Merchant not found. id=" + it.targetId()));

                    return new FavoriteResponse(
                            it.id(),
                            FavoriteTargetType.MERCHANT,
                            merchant.id(),
                            merchant.name(),
                            null, null, null, null, null, null,
                            it.favoritedAt(),
                            null
                    );
                })
                .toList();
    }
}
