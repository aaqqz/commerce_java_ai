package io.dodn.commerce.core.api.controller.v1.response;

import io.dodn.commerce.core.domain.OrderSummary;
import io.dodn.commerce.core.enums.OrderState;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class OrderListResponse {
    private final String key;
    private final String name;
    private final BigDecimal totalPrice;
    private final OrderState state;

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
