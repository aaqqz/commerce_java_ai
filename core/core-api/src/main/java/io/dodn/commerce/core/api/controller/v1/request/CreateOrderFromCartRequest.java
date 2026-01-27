package io.dodn.commerce.core.api.controller.v1.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderFromCartRequest {
    private Set<Long> cartItemIds;
}
