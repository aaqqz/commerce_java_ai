package io.dodn.commerce.core.api.controller.v1.response;

import io.dodn.commerce.core.domain.OrderSummary;
import io.dodn.commerce.core.enums.OrderState;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public record OrderListResponse(
        String key,
        String name,
        BigDecimal totalPrice,
        OrderState state
) {
    private static OrderListResponse of(OrderSummary order) {
        return new OrderListResponse(
                order.getKey(),
                order.getName(),
                order.getTotalPrice(),
                order.getState()
        );
    }

    public static List<OrderListResponse> of(List<OrderSummary> orders) {
        return orders.stream()
                .map(OrderListResponse::of)
                .collect(Collectors.toList());
    }
}
