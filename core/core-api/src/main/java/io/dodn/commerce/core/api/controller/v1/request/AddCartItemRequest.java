package io.dodn.commerce.core.api.controller.v1.request;

import io.dodn.commerce.core.domain.AddCartItem;
import io.dodn.commerce.core.support.error.CoreException;
import io.dodn.commerce.core.support.error.ErrorType;

public record AddCartItemRequest(
        Long productId,
        Long quantity
) {
    public AddCartItem toAddCartItem() {
        if (quantity <= 0)
            throw new CoreException(ErrorType.INVALID_REQUEST);

        return new AddCartItem(productId, quantity);
    }
}
