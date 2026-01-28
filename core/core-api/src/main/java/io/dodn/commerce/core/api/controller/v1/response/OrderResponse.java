package io.dodn.commerce.core.api.controller.v1.response;

import io.dodn.commerce.core.domain.Order;
import io.dodn.commerce.core.enums.OrderState;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public record OrderResponse(
        String key,
        String name,
        BigDecimal totalPrice,
        OrderState state,
        List<OrderItemResponse> items
) {
    public static OrderResponse of(Order order) {
        return new OrderResponse(
                order.getKey(),
                order.getName(),
                order.getTotalPrice(),
                order.getState(),
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
                        .collect(Collectors.toList())
        );
    }
}
