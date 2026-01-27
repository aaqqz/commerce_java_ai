package io.dodn.commerce.core.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class ReviewContent {
    private final BigDecimal rate;
    private final String content;
}
