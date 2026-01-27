package io.dodn.commerce.core.api.controller.v1.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class CartResponse {
    private final List<CartItemResponse> items;
}
