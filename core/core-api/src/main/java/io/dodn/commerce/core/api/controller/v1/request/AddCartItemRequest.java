package io.dodn.commerce.core.api.controller.v1.request;

import io.dodn.commerce.core.domain.AddCartItem;
import io.dodn.commerce.core.support.error.CoreException;
import io.dodn.commerce.core.support.error.ErrorType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AddCartItemRequest {
    private Long productId;
    private Long quantity;

    public AddCartItem toAddCartItem() {
        if (quantity <= 0) {
            throw new CoreException(ErrorType.INVALID_REQUEST);
        }
        return new AddCartItem(productId, quantity);
    }
}
