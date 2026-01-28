package io.dodn.commerce.core.api.controller.v1.response;

import io.dodn.commerce.core.domain.Order;
import io.dodn.commerce.core.domain.OwnedCoupon;
import io.dodn.commerce.core.domain.PointBalance;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

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
                order.getKey(),
                order.getName(),
                order.getTotalPrice(),
                order.getItems().stream()
                        .map(it -> new OrderItemResponse(
                                it.getProductId(),
                                it.getProductName(),
                                it.getThumbnailUrl(),
                                it.getShortDescription(),
                                it.getQuantity(),
                                it.getUnitPrice(),
                                it.getTotalPrice()
                        ))
                        .collect(Collectors.toList()),
                OwnedCouponResponse.of(coupons),
                point.balance()
        );
    }
}
