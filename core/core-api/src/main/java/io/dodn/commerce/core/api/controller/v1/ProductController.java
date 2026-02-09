package io.dodn.commerce.core.api.controller.v1;

import io.dodn.commerce.core.api.controller.v1.response.ProductDetailResponse;
import io.dodn.commerce.core.api.controller.v1.response.ProductResponse;
import io.dodn.commerce.core.api.facade.ProductFacade;
import io.dodn.commerce.core.support.OffsetLimit;
import io.dodn.commerce.core.support.response.ApiResponse;
import io.dodn.commerce.core.support.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProductController {
    private final ProductFacade productFacade;

    @GetMapping("/v1/products")
    public ApiResponse<PageResponse<ProductResponse>> findProducts(@RequestParam Long categoryId, @RequestParam Integer offset, @RequestParam Integer limit) {
        var page = productFacade.findProducts(categoryId, OffsetLimit.of(offset, limit));
        return ApiResponse.success(PageResponse.of(page.content(), page.hasNext()));
    }

    @GetMapping("/v1/products/{productId}")
    public ApiResponse<ProductDetailResponse> findProduct(@PathVariable Long productId) {
        return ApiResponse.success(productFacade.findProduct(productId));
    }
}
