package io.dodn.commerce.core.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class NewOrder {
    private final Long userId;
    private final List<NewOrderItem> items;
}
