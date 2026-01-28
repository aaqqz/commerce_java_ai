package io.dodn.commerce.storage.db.core;

import io.dodn.commerce.core.enums.PointType;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Entity
@Table(name = "point_history")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PointHistoryEntity extends BaseEntity {
    private Long userId;
    private PointType type;
    private Long referenceId;
    private BigDecimal amount;
    private BigDecimal balanceAfter;

    public static PointHistoryEntity create(
            Long userId,
            PointType type,
            Long referenceId,
            BigDecimal amount,
            BigDecimal balanceAfter
    ) {
        PointHistoryEntity history = new PointHistoryEntity();
        history.userId = userId;
        history.type = type;
        history.referenceId = referenceId;
        history.amount = amount;
        history.balanceAfter = balanceAfter;

        return history;
    }
}
