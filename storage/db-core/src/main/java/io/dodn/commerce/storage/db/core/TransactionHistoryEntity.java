package io.dodn.commerce.storage.db.core;

import io.dodn.commerce.core.enums.TransactionType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transaction_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TransactionHistoryEntity extends BaseEntity {
    @Enumerated(EnumType.STRING)
    private TransactionType type;

    private Long userId;
    private Long orderId;
    private Long paymentId;
    private String externalPaymentKey;
    private BigDecimal amount;
    private String message;
    private LocalDateTime occurredAt;

    public TransactionHistoryEntity(
            TransactionType type,
            Long userId,
            Long orderId,
            Long paymentId,
            String externalPaymentKey,
            BigDecimal amount,
            String message,
            LocalDateTime occurredAt
    ) {
        this.type = type;
        this.userId = userId;
        this.orderId = orderId;
        this.paymentId = paymentId;
        this.externalPaymentKey = externalPaymentKey;
        this.amount = amount;
        this.message = message;
        this.occurredAt = occurredAt;
    }
}
