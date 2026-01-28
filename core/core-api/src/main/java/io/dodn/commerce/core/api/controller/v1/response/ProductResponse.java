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
                        it.getName(),
                        it.getThumbnailUrl(),
                        it.getDescription(),
                        it.getShortDescription(),
                        it.getPrice().getCostPrice(),
                        it.getPrice().getSalesPrice(),
                        it.getPrice().getDiscountedPrice()
                ))
                .collect(Collectors.toList());
    }
}
