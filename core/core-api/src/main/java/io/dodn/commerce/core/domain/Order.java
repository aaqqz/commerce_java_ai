package io.dodn.commerce.core.domain;

import io.dodn.commerce.core.enums.OrderState;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@AllArgsConstructor
public class Order {
    private final Long id;
    private final String key;
    private final String name;
    private final Long userId;
    private final BigDecimal totalPrice;
    private final OrderState state;
    private final List<OrderItem> items;
}
