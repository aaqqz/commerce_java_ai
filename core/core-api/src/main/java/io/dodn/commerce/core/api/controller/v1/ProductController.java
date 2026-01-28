package io.dodn.commerce.core.api.controller.v1;

import io.dodn.commerce.core.api.controller.v1.response.ProductDetailResponse;
import io.dodn.commerce.core.api.controller.v1.response.ProductResponse;
import io.dodn.commerce.core.domain.CouponService;
import io.dodn.commerce.core.domain.ProductSectionService;
import io.dodn.commerce.core.domain.ProductService;
import io.dodn.commerce.core.domain.ReviewService;
import io.dodn.commerce.core.domain.ReviewTarget;
import io.dodn.commerce.core.enums.ReviewTargetType;
import io.dodn.commerce.core.support.OffsetLimit;
import io.dodn.commerce.core.support.response.ApiResponse;
import io.dodn.commerce.core.support.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final ProductSectionService productSectionService;
    private final ReviewService reviewService;
    private final CouponService couponService;

    @GetMapping("/v1/products")
    public ApiResponse<PageResponse<ProductResponse>> findProducts(
            @RequestParam Long categoryId,
            @RequestParam Integer offset,
            @RequestParam Integer limit
    ) {
        var result = productService.findProducts(categoryId, OffsetLimit.of(offset, limit));

        return ApiResponse.success(PageResponse.of(ProductResponse.of(result.content()), result.hasNext()));
    }

    @GetMapping("/v1/products/{productId}")
    public ApiResponse<ProductDetailResponse> findProduct(@PathVariable Long productId) {
        var product = productService.findProduct(productId);
        var sections = productSectionService.findSections(productId);
        var rateSummary = reviewService.findRateSummary(new ReviewTarget(ReviewTargetType.PRODUCT, productId));
        // NOTE: 별도 API 가 나을까?
        var coupons = couponService.getCouponsForProducts(List.of(productId));

        return ApiResponse.success(ProductDetailResponse.of(product, sections, rateSummary, coupons));
    }
}
