package io.dodn.commerce.core.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NewOrderItem {
    private final Long productId;
    private final Long quantity;
}
