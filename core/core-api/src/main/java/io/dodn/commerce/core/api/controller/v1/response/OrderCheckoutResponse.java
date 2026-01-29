package io.dodn.commerce.core.api.controller.v1.response;

import io.dodn.commerce.core.domain.Order;
import io.dodn.commerce.core.domain.OwnedCoupon;
import io.dodn.commerce.core.domain.PointBalance;

import java.math.BigDecimal;
import java.util.List;

public record OrderCheckoutResponse(
        String key,
        String name,
        BigDecimal totalPrice,
        List<OrderItemResponse> items,
        List<OwnedCouponResponse> usableCoupons,
        BigDecimal usablePoint
) {
    public static OrderCheckoutResponse of(Order order, List<OwnedCoupon> coupons, PointBalance point) {
        return new OrderCheckoutResponse(
                order.key(),
                order.name(),
                order.totalPrice(),
                order.items().stream()
                        .map(it -> new OrderItemResponse(
                                it.productId(),
                                it.productName(),
                                it.thumbnailUrl(),
                                it.shortDescription(),
                                it.quantity(),
                                it.unitPrice(),
                                it.totalPrice()
                        ))
                        .toList(),
                OwnedCouponResponse.of(coupons),
                point.balance()
        );
    }
}
