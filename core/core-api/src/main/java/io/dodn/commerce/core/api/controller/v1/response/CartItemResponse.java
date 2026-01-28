package io.dodn.commerce.core.api.controller.v1.response;

import io.dodn.commerce.core.domain.CartItem;

import java.math.BigDecimal;

public record CartItemResponse(
        Long id,
        Long productId,
        String productName,
        String thumbnailUrl,
        String description,
        String shortDescription,
        BigDecimal costPrice,
        BigDecimal salesPrice,
        BigDecimal discountedPrice,
        Long quantity
) {

    public static CartItemResponse of(CartItem cartItem) {
        return new CartItemResponse(
                cartItem.getId(),
                cartItem.getProduct().getId(),
                cartItem.getProduct().getName(),
                cartItem.getProduct().getThumbnailUrl(),
                cartItem.getProduct().getDescription(),
                cartItem.getProduct().getShortDescription(),
                cartItem.getProduct().getPrice().getCostPrice(),
                cartItem.getProduct().getPrice().getSalesPrice(),
                cartItem.getProduct().getPrice().getDiscountedPrice(),
                cartItem.getQuantity()
        );
    }
}
