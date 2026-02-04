package io.dodn.commerce.core.api.controller.v1.response;

import io.dodn.commerce.core.domain.*;

import java.math.BigDecimal;
import java.util.List;

public record ProductDetailResponse(
        String name,
        String thumbnailUrl,
        String description,
        String shortDescription,
        BigDecimal costPrice,
        BigDecimal salesPrice,
        BigDecimal discountedPrice,
        BigDecimal rate,
        Long rateCount,
        List<ProductOptionResponse> options,
        List<ProductSectionResponse> sections,
        List<CouponResponse> coupons
) {
    public static ProductDetailResponse of(
            Product product,
            List<ProductOption> options,
            List<ProductSection> sections,
            RateSummary rateSummary,
            List<Coupon> coupons
    ) {
        return new ProductDetailResponse(
                product.name(),
                product.thumbnailUrl(),
                product.description(),
                product.shortDescription(),
                product.price().costPrice(),
                product.price().salesPrice(),
                product.price().discountedPrice(),
                rateSummary.rate(),
                rateSummary.count(),
                options.stream()
                        .map(it -> new ProductOptionResponse(
                                it.id(),
                                it.name(),
                                it.description(),
                                it.price().costPrice(),
                                it.price().salesPrice(),
                                it.price().discountedPrice()
                        ))
                        .toList(),
                sections.stream()
                        .map(it -> new ProductSectionResponse(it.type(), it.content()))
                        .toList(),
                coupons.stream()
                        .map(CouponResponse::of)
                        .toList()
        );
    }
}
