package io.dodn.commerce.core.domain;

import java.math.BigDecimal;

public record ReviewProcessResult(
        Long id,
        ReviewFormat format
) {
    public BigDecimal pointAmount() {
        return switch (format) {
            case TEXT -> PointAmount.TEXT_REVIEW;
            case IMAGE -> PointAmount.IMAGE_REVIEW;
            default -> throw new IllegalStateException("Unknown format: " + format);
        };
    }
}
