package io.dodn.commerce.core.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class PointBalance {
    private final Long userId;
    private final BigDecimal balance;
}
