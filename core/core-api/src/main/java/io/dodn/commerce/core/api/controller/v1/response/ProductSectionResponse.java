package io.dodn.commerce.core.api.controller.v1.response;

import io.dodn.commerce.core.enums.ProductSectionType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductSectionResponse {
    private final ProductSectionType type;
    private final String content;
}
