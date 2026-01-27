package io.dodn.commerce.core.api.controller.v1;

import io.dodn.commerce.core.api.controller.v1.request.AddCartItemRequest;
import io.dodn.commerce.core.api.controller.v1.request.ModifyCartItemRequest;
import io.dodn.commerce.core.api.controller.v1.response.CartItemResponse;
import io.dodn.commerce.core.api.controller.v1.response.CartResponse;
import io.dodn.commerce.core.domain.CartService;
import io.dodn.commerce.core.domain.User;
import io.dodn.commerce.core.support.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @GetMapping("/v1/cart")
    public ApiResponse<CartResponse> getCart(User user) {
        var cart = cartService.getCart(user);
        return ApiResponse.success(new CartResponse(
                cart.getItems().stream()
                        .map(CartItemResponse::of)
                        .collect(Collectors.toList())
        ));
    }

    @PostMapping("/v1/cart/items")
    public ApiResponse<Object> addCartItem(User user, @RequestBody AddCartItemRequest request) {
        cartService.addCartItem(user, request.toAddCartItem());
        return ApiResponse.success();
    }

    @PutMapping("/v1/cart/items/{cartItemId}")
    public ApiResponse<Object> modifyCartItem(
            User user,
            @PathVariable Long cartItemId,
            @RequestBody ModifyCartItemRequest request) {
        cartService.modifyCartItem(user, request.toModifyCartItem(cartItemId));
        return ApiResponse.success();
    }

    @DeleteMapping("/v1/cart/items/{cartItemId}")
    public ApiResponse<Object> deleteCartItem(User user, @PathVariable Long cartItemId) {
        cartService.deleteCartItem(user, cartItemId);
        return ApiResponse.success();
    }
}
