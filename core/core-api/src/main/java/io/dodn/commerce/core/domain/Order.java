package io.dodn.commerce.core.domain;

import io.dodn.commerce.core.enums.OrderState;

import java.math.BigDecimal;
import java.util.List;

public record Order(
        Long id,
        String key,
        String name,
        Long userId,
        BigDecimal totalPrice,
        OrderState state,
        List<OrderItem> items
) {
}
