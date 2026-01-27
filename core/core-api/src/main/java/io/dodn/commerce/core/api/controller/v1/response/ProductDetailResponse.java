package io.dodn.commerce.core.api.controller.v1.response;

import io.dodn.commerce.core.domain.Coupon;
import io.dodn.commerce.core.domain.Product;
import io.dodn.commerce.core.domain.ProductSection;
import io.dodn.commerce.core.domain.RateSummary;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class ProductDetailResponse {
    private final String name;
    private final String thumbnailUrl;
    private final String description;
    private final String shortDescription;
    private final BigDecimal costPrice;
    private final BigDecimal salesPrice;
    private final BigDecimal discountedPrice;
    private final BigDecimal rate;
    private final Long rateCount;
    private final List<ProductSectionResponse> sections;
    private final List<CouponResponse> coupons;

    public ProductDetailResponse(
            Product product,
            List<ProductSection> sections,
            RateSummary rateSummary,
            List<Coupon> coupons) {
        this.name = product.getName();
        this.thumbnailUrl = product.getThumbnailUrl();
        this.description = product.getDescription();
        this.shortDescription = product.getShortDescription();
        this.costPrice = product.getPrice().getCostPrice();
        this.salesPrice = product.getPrice().getSalesPrice();
        this.discountedPrice = product.getPrice().getDiscountedPrice();
        this.rate = rateSummary.getRate();
        this.rateCount = rateSummary.getCount();
        this.sections = sections.stream()
                .map(it -> new ProductSectionResponse(it.getType(), it.getContent()))
                .collect(Collectors.toList());
        this.coupons = coupons.stream()
                .map(CouponResponse::of)
                .collect(Collectors.toList());
    }
}
