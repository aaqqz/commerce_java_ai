package io.dodn.commerce.core.domain;

import java.util.List;

public record NewOrder(
        Long userId,
        List<NewOrderItem> items
) {
}
