package io.dodn.commerce.core.domain;

import io.dodn.commerce.core.enums.PointType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PointHistory {
    private final Long id;
    private final Long userId;
    private final PointType type;
    private final Long referenceId;
    private final BigDecimal amount;
    private final LocalDateTime appliedAt;
}
