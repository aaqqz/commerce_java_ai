package io.dodn.commerce.core.domain;

import java.time.LocalDateTime;

public record Product(
        Long id,
        String name,
        String thumbnailUrl,
        String description,
        String shortDescription,
        Price price,
        LocalDateTime updatedAt
) {
}
