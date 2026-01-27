package io.dodn.commerce.core.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ModifyCartItem {
    private final Long cartItemId;
    private final Long quantity;
}
