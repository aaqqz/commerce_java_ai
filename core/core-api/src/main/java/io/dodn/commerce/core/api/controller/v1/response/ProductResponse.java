package io.dodn.commerce.core.api.controller.v1.response;

import io.dodn.commerce.core.domain.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record ProductResponse(
        String name,
        String thumbnailUrl,
        String description,
        String shortDescription,
        BigDecimal costPrice,
        BigDecimal salesPrice,
        BigDecimal discountedPrice,
        Long favoriteCount,
        Long orderCount,
        Boolean isUnique
) {
        public static List<ProductResponse> of(List<Product> products, Map<Long, Long> favoriteCountMap, Map<Long, Long> orderCountMap) {
            return products.stream()
                    .map(it -> new ProductResponse(
                            it.name(),
                            it.thumbnailUrl(),
                            it.description(),
                            it.shortDescription(),
                            it.price().costPrice(),
                            it.price().salesPrice(),
                            it.price().discountedPrice(),
                            favoriteCountMap.getOrDefault(it.id(), 0L),
                            orderCountMap.getOrDefault(it.id(), 0L),
                            favoriteCountMap.getOrDefault(it.id(), 0L) == 0L && orderCountMap.getOrDefault(it.id(), 0L) == 0L
                    ))
                    .toList();
    }
}
