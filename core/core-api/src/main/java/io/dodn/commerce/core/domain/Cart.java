package io.dodn.commerce.core.domain;

import io.dodn.commerce.core.support.error.CoreException;
import io.dodn.commerce.core.support.error.ErrorType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class Cart {
    private final Long userId;
    private final List<CartItem> items;

    public NewOrder toNewOrder(Set<Long> targetItemIds) {
        if (items.isEmpty()) {
            throw new CoreException(ErrorType.INVALID_REQUEST);
        }
        return new NewOrder(
                userId,
                items.stream()
                        .filter(item -> targetItemIds.contains(item.getId()))
                        .map(item -> new NewOrderItem(item.getProduct().getId(), item.getQuantity()))
                        .collect(Collectors.toList())
        );
    }
}
