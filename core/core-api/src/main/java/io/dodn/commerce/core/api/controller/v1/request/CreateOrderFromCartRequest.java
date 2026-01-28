package io.dodn.commerce.core.api.controller.v1.request;

import java.util.Set;

public record CreateOrderFromCartRequest(
        Set<Long> cartItemIds
) {
}
