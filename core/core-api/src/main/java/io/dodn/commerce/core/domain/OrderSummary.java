package io.dodn.commerce.core.domain;

import io.dodn.commerce.core.enums.OrderState;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class OrderSummary {
    private final Long id;
    private final String key;
    private final String name;
    private final Long userId;
    private final BigDecimal totalPrice;
    private final OrderState state;
}
