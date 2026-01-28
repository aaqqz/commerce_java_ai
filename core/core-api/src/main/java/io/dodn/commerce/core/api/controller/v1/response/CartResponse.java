package io.dodn.commerce.core.api.controller.v1.response;

import java.util.List;

public record CartResponse(
        List<CartItemResponse> items
) {
}
