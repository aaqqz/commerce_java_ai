package io.dodn.commerce.core.api.controller.v1.response;

import io.dodn.commerce.core.domain.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class ProductResponse {
    private final String name;
    private final String thumbnailUrl;
    private final String description;
    private final String shortDescription;
    private final BigDecimal costPrice;
    private final BigDecimal salesPrice;
    private final BigDecimal discountedPrice;

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
