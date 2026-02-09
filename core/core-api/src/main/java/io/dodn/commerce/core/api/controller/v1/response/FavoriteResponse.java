package io.dodn.commerce.core.api.controller.v1.response;

import io.dodn.commerce.core.domain.Favorite;
import io.dodn.commerce.core.domain.Product;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public record FavoriteResponse(
        Long id,
        Long productId,
        String productName,
        String productThumbnailUrl,
        BigDecimal costPrice,
        BigDecimal salesPrice,
        BigDecimal discountedPrice,
        LocalDateTime favoritedAt
) {
    public static List<FavoriteResponse> of(List<Favorite> favorite, Map<Long, Product> productMap) {
        return favorite.stream()
                .map(it -> {
                    var product = Optional.ofNullable(productMap.get(it.productId()))
                            .orElseThrow(() -> new IllegalStateException("Product not found. id=" + it.productId()));

                    return new FavoriteResponse(
                            it.id(),
                            it.productId(),
                            product.name(),
                            product.thumbnailUrl(),
                            product.price().costPrice(),
                            product.price().salesPrice(),
                            product.price().discountedPrice(),
                            it.favoritedAt()
                    );
                })
                .toList();
    }
}
