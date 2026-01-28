package io.dodn.commerce.core.domain;

public record Review(
        Long id,
        Long userId,
        ReviewTarget target,
        ReviewContent content
) {
}
