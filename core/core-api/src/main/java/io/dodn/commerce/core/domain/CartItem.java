package io.dodn.commerce.core.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CartItem {
    private final Long id;
    private final Product product;
    private final Long quantity;
}
