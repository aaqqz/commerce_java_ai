package io.dodn.commerce.core.domain;

import io.dodn.commerce.core.support.error.CoreException;
import io.dodn.commerce.core.support.error.ErrorType;

import java.util.List;
import java.util.Set;

public record Cart(
        Long userId,
        List<CartItem> items
) {
    public NewOrder toNewOrder(Set<Long> targetItemIds) {
        if (items.isEmpty()) {
            throw new CoreException(ErrorType.INVALID_REQUEST);
        }

        return new NewOrder(
                userId,
                items.stream()
                        .filter(item -> targetItemIds.contains(item.id()))
                        .map(item -> new NewOrderItem(item.product().id(), item.quantity()))
                        .toList()
        );
    }
}
