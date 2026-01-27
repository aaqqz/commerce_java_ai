package io.dodn.commerce.core.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class Price {
    private final BigDecimal costPrice;
    private final BigDecimal salesPrice;
    private final BigDecimal discountedPrice;
}
