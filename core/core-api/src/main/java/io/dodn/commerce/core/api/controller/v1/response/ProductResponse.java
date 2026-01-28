package io.dodn.commerce.core.api.controller.v1.response;

import io.dodn.commerce.core.domain.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public record ProductResponse(
        String name,
        String thumbnailUrl,
        String description,
        String shortDescription,
        BigDecimal costPrice,
        BigDecimal salesPrice,
        BigDecimal discountedPrice
) {
    public static List<ProductResponse> of(List<Product> products) {
        return products.stream()
                .map(it -> new ProductResponse(
                        it.name(),
                        it.thumbnailUrl(),
                        it.description(),
                        it.shortDescription(),
                        it.price().costPrice(),
                        it.price().salesPrice(),
                        it.price().discountedPrice()
                ))
                .collect(Collectors.toList());
    }
}
