package io.dodn.commerce.core.domain;

import io.dodn.commerce.core.enums.PointType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PointHistory(
        Long id,
        Long userId,
        PointType type,
        Long referenceId,
        BigDecimal amount,
        LocalDateTime appliedAt
) {
}
