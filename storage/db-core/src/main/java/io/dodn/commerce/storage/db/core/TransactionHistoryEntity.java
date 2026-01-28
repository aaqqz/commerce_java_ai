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

@Getter
@Entity
@Table(name = "transaction_history")
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

    public static TransactionHistoryEntity create(
            TransactionType type,
            Long userId,
            Long orderId,
            Long paymentId,
            String externalPaymentKey,
            BigDecimal amount,
            String message,
            LocalDateTime occurredAt
    ) {
        TransactionHistoryEntity history = new TransactionHistoryEntity();
        history.type = type;
        history.userId = userId;
        history.orderId = orderId;
        history.paymentId = paymentId;
        history.externalPaymentKey = externalPaymentKey;
        history.amount = amount;
        history.message = message;
        history.occurredAt = occurredAt;

        return history;
    }
}
