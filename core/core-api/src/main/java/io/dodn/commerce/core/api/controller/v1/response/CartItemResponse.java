package io.dodn.commerce.core.api.controller.v1.response;

import io.dodn.commerce.core.domain.CartItem;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class CartItemResponse {
    private final Long id;
    private final Long productId;
    private final String productName;
    private final String thumbnailUrl;
    private final String description;
    private final String shortDescription;
    private final BigDecimal costPrice;
    private final BigDecimal salesPrice;
    private final BigDecimal discountedPrice;
    private final Long quantity;

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
