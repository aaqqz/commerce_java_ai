package io.dodn.commerce.core.domain;

import io.dodn.commerce.core.enums.ReviewTargetType;

public record ReviewTarget(
        ReviewTargetType type,
        Long id
) {
}
