package io.dodn.commerce.core.api.controller.v1.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateOrderResponse {
    private final String orderKey;
}
