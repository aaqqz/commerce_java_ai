package io.dodn.commerce.core.api.controller.v1.response;

import io.dodn.commerce.core.domain.Favorite;
import io.dodn.commerce.core.domain.Product;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record FavoriteResponse(
        Long id,
        Long productId,
        LocalDateTime favoritedAt,
        // 상품 정보 추가 (Flatten 구조)
        String productName,
        String productThumbnailUrl,
        BigDecimal productCostPrice,
        BigDecimal productSalesPrice,
        BigDecimal productDiscountedPrice
) {
    public static FavoriteResponse of(Favorite favorite, Product product) {
        // 상품이 삭제된 경우 null 처리
        if (product == null) {
            return new FavoriteResponse(
                    favorite.id(),
                    favorite.productId(),
                    favorite.favoritedAt(),
                    null,
                    null,
                    null,
                    null,
                    null
            );
        }

        return new FavoriteResponse(
                favorite.id(),
                favorite.productId(),
                favorite.favoritedAt(),
                product.name(),
                product.thumbnailUrl(),
                product.price().costPrice(),
                product.price().salesPrice(),
                product.price().discountedPrice()
        );
    }
}
