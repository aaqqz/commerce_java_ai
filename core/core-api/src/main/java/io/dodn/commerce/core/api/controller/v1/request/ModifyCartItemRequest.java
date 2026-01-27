package io.dodn.commerce.core.api.controller.v1.request;

import io.dodn.commerce.core.domain.ModifyCartItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ModifyCartItemRequest {
    private Long quantity;

    public ModifyCartItem toModifyCartItem(Long cartItemId) {
        return new ModifyCartItem(cartItemId, quantity);
    }
}
