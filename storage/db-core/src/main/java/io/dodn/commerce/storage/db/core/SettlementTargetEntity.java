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
import java.time.LocalDate;

@Entity
@Table(name = "settlement_target")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SettlementTargetEntity extends BaseEntity {
    private Long merchantId;
    private LocalDate settlementDate;
    private BigDecimal targetAmount;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    private Long transactionId;
    private Long orderId;
    private Long productId;
    private Long quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;

    public SettlementTargetEntity(
            Long merchantId,
            LocalDate settlementDate,
            BigDecimal targetAmount,
            TransactionType transactionType,
            Long transactionId,
            Long orderId,
            Long productId,
            Long quantity,
            BigDecimal unitPrice,
            BigDecimal totalPrice
    ) {
        this.merchantId = merchantId;
        this.settlementDate = settlementDate;
        this.targetAmount = targetAmount;
        this.transactionType = transactionType;
        this.transactionId = transactionId;
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
    }
}
