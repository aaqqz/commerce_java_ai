package io.dodn.commerce.core.api.controller.v1;

import io.dodn.commerce.core.api.controller.v1.request.CreateOrderFromCartRequest;
import io.dodn.commerce.core.api.controller.v1.request.CreateOrderRequest;
import io.dodn.commerce.core.api.controller.v1.response.CreateOrderResponse;
import io.dodn.commerce.core.api.controller.v1.response.OrderCheckoutResponse;
import io.dodn.commerce.core.api.controller.v1.response.OrderListResponse;
import io.dodn.commerce.core.api.controller.v1.response.OrderResponse;
import io.dodn.commerce.core.domain.CartService;
import io.dodn.commerce.core.domain.OrderService;
import io.dodn.commerce.core.domain.OwnedCouponService;
import io.dodn.commerce.core.domain.PointService;
import io.dodn.commerce.core.domain.User;
import io.dodn.commerce.core.enums.OrderState;
import io.dodn.commerce.core.support.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final CartService cartService;
    private final OwnedCouponService ownedCouponService;
    private final PointService pointService;

    @PostMapping("/v1/orders")
    public ApiResponse<CreateOrderResponse> create(
            User user,
            @RequestBody CreateOrderRequest request) {
        String key = orderService.create(user, request.toNewOrder(user));
        return ApiResponse.success(new CreateOrderResponse(key));
    }

    @PostMapping("/v1/cart-orders")
    public ApiResponse<CreateOrderResponse> createFromCart(
            User user,
            @RequestBody CreateOrderFromCartRequest request) {
        var cart = cartService.getCart(user);
        String key = orderService.create(user, cart.toNewOrder(request.getCartItemIds()));
        return ApiResponse.success(new CreateOrderResponse(key));
    }

    @GetMapping("/v1/orders/{orderKey}/checkout")
    public ApiResponse<OrderCheckoutResponse> findOrderForCheckout(
            User user,
            @PathVariable String orderKey) {
        var order = orderService.getOrder(user, orderKey, OrderState.CREATED);
        var ownedCoupons = ownedCouponService.getOwnedCouponsForCheckout(
                user,
                order.getItems().stream()
                        .map(io.dodn.commerce.core.domain.OrderItem::getProductId)
                        .collect(Collectors.toList())
        );
        var pointBalance = pointService.balance(user);
        return ApiResponse.success(OrderCheckoutResponse.of(order, ownedCoupons, pointBalance));
    }

    @GetMapping("/v1/orders")
    public ApiResponse<List<OrderListResponse>> getOrders(User user) {
        var orders = orderService.getOrders(user);
        return ApiResponse.success(OrderListResponse.of(orders));
    }

    @GetMapping("/v1/orders/{orderKey}")
    public ApiResponse<OrderResponse> getOrder(
            User user,
            @PathVariable String orderKey) {
        var order = orderService.getOrder(user, orderKey, OrderState.PAID);
        return ApiResponse.success(OrderResponse.of(order));
    }
}
