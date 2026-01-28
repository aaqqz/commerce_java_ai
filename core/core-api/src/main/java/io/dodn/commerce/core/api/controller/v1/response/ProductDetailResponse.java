package io.dodn.commerce.core.api.controller.v1.response;

import io.dodn.commerce.core.domain.Coupon;
import io.dodn.commerce.core.domain.Product;
import io.dodn.commerce.core.domain.ProductSection;
import io.dodn.commerce.core.domain.RateSummary;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

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
        List<ProductSectionResponse> sections,
        List<CouponResponse> coupons
) {
    public static ProductDetailResponse of(
            Product product,
            List<ProductSection> sections,
            RateSummary rateSummary,
            List<Coupon> coupons
    ) {
        return new ProductDetailResponse(
                product.getName(),
                product.getThumbnailUrl(),
                product.getDescription(),
                product.getShortDescription(),
                product.getPrice().getCostPrice(),
                product.getPrice().getSalesPrice(),
                product.getPrice().getDiscountedPrice(),
                rateSummary.getRate(),
                rateSummary.getCount(),
                sections.stream()
                        .map(it -> new ProductSectionResponse(it.getType(), it.getContent()))
                        .collect(Collectors.toList()),
                coupons.stream()
                        .map(CouponResponse::of)
                        .collect(Collectors.toList())
        );
    }
}
