package io.dodn.commerce.storage.db.core;

import io.dodn.commerce.core.enums.PointType;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "point_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PointHistoryEntity extends BaseEntity {
    private Long userId;
    private PointType type;
    private Long referenceId;
    private BigDecimal amount;
    private BigDecimal balanceAfter;

    public PointHistoryEntity(
            Long userId,
            PointType type,
            Long referenceId,
            BigDecimal amount,
            BigDecimal balanceAfter
    ) {
        this.userId = userId;
        this.type = type;
        this.referenceId = referenceId;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
    }
}
