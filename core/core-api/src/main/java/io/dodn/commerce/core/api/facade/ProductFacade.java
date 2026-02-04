package io.dodn.commerce.core.api.facade;

import io.dodn.commerce.core.api.controller.v1.response.ProductDetailResponse;
import io.dodn.commerce.core.api.controller.v1.response.ProductResponse;
import io.dodn.commerce.core.domain.CouponService;
import io.dodn.commerce.core.domain.FavoriteService;
import io.dodn.commerce.core.domain.OrderService;
import io.dodn.commerce.core.domain.Product;
import io.dodn.commerce.core.domain.ProductOptionService;
import io.dodn.commerce.core.domain.ProductSectionService;
import io.dodn.commerce.core.domain.ProductService;
import io.dodn.commerce.core.domain.ReviewService;
import io.dodn.commerce.core.domain.ReviewTarget;
import io.dodn.commerce.core.enums.ReviewTargetType;
import io.dodn.commerce.core.support.OffsetLimit;
import io.dodn.commerce.core.support.Page;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductFacade {
    private final ProductService productService;
    private final ProductSectionService productSectionService;
    private final ReviewService reviewService;
    private final CouponService couponService;
    private final FavoriteService favoriteService;
    private final OrderService orderService;

    public Page<ProductResponse> findProducts(Long categoryId, OffsetLimit offsetLimit) {
        var productPage = productService.findProducts(categoryId, offsetLimit);
        List<Product> products = productPage.content();
        List<Long> productIds = products.stream()
            .map(Product::id)
            .toList();

        LocalDateTime now = LocalDateTime.now();
        Map<Long, Long> favoriteCountMap = favoriteService.recentCount(productIds, now.minusDays(ProductStatsSpec.FAVORITE_COUNT_DAYS));
        Map<Long, Long> orderCountMap = orderService.recentCount(productIds, now.minusDays(ProductStatsSpec.ORDER_COUNT_DAYS));

        List<ProductResponse> responses = ProductResponse.of(productPage.content(), favoriteCountMap, orderCountMap);
        return new Page<>(responses, productPage.hasNext());
    }

    public ProductDetailResponse findProduct(Long productId) {
        var product = productService.findProduct(productId);
        var options = productService.findOptions(productId);
        var sections = productSectionService.findSections(productId);
        var rateSummary = reviewService.findRateSummary(new ReviewTarget(ReviewTargetType.PRODUCT, productId));
        var coupons = couponService.getCouponsForProducts(List.of(productId));

        return ProductDetailResponse.of(product, options, sections, rateSummary, coupons);
    }
}
