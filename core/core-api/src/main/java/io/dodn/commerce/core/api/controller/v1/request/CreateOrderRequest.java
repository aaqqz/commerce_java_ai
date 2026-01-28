package io.dodn.commerce.core.api.controller.v1.request;

import io.dodn.commerce.core.domain.NewOrder;
import io.dodn.commerce.core.domain.NewOrderItem;
import io.dodn.commerce.core.domain.User;
import io.dodn.commerce.core.support.error.CoreException;
import io.dodn.commerce.core.support.error.ErrorType;

import java.util.List;

public record CreateOrderRequest(
        Long productId,
        Long quantity
) {
    public NewOrder toNewOrder(User user) {
        if (quantity <= 0) {
            throw new CoreException(ErrorType.INVALID_REQUEST);
        }
        return new NewOrder(
                user.id(),
                List.of(new NewOrderItem(productId, quantity))
        );
    }
}
