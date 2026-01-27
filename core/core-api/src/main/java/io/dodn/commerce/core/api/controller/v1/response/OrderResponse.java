package io.dodn.commerce.core.api.controller.v1.response;

import io.dodn.commerce.core.domain.Order;
import io.dodn.commerce.core.enums.OrderState;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class OrderResponse {
    private final String key;
    private final String name;
    private final BigDecimal totalPrice;
    private final OrderState state;
    private final List<OrderItemResponse> items;

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
