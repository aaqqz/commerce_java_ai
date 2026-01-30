package io.dodn.commerce.core.api.facade;

import io.dodn.commerce.core.api.controller.v1.response.ProductDetailResponse;
import io.dodn.commerce.core.domain.CouponService;
import io.dodn.commerce.core.domain.ProductSectionService;
import io.dodn.commerce.core.domain.ProductService;
import io.dodn.commerce.core.domain.ReviewService;
import io.dodn.commerce.core.domain.ReviewTarget;
import io.dodn.commerce.core.enums.ReviewTargetType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductFacade {
    private final ProductService productService;
    private final ProductSectionService productSectionService;
    private final ReviewService reviewService;
    private final CouponService couponService;

    public ProductDetailResponse findProduct(Long productId) {
        var product = productService.findProduct(productId);
        var sections = productSectionService.findSections(productId);
        var rateSummary = reviewService.findRateSummary(new ReviewTarget(ReviewTargetType.PRODUCT, productId));
        var coupons = couponService.getCouponsForProducts(List.of(productId));

        return ProductDetailResponse.of(product, sections, rateSummary, coupons);
    }
}
