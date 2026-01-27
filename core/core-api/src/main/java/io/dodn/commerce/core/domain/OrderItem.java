package io.dodn.commerce.core.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class OrderItem {
    private final Long orderId;
    private final Long productId;
    private final String productName;
    private final String thumbnailUrl;
    private final String shortDescription;
    private final Long quantity;
    private final BigDecimal unitPrice;
    private final BigDecimal totalPrice;
}
