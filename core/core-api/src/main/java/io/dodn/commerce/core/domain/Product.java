package io.dodn.commerce.core.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Product {
    private final Long id;
    private final String name;
    private final String thumbnailUrl;
    private final String description;
    private final String shortDescription;
    private final Price price;
}
