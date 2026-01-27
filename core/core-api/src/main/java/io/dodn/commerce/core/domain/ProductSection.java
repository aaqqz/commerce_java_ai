package io.dodn.commerce.core.domain;

import io.dodn.commerce.core.enums.ProductSectionType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductSection {
    private final ProductSectionType type;
    private final String content;
}
