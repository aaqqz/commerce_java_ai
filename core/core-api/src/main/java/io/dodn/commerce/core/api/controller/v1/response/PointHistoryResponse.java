package io.dodn.commerce.core.api.controller.v1.response;

import io.dodn.commerce.core.enums.PointType;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PointHistoryResponse(
        PointType type,
        BigDecimal amount,
        LocalDateTime appliedAt
) {
}
