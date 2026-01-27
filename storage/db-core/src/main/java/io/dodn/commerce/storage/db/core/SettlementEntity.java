package io.dodn.commerce.storage.db.core;

import io.dodn.commerce.core.enums.SettlementState;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(
        name = "settlement",
        indexes = {
                @Index(name = "udx_settlement_merchant", columnList = "settlementDate, merchantId", unique = true)
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SettlementEntity extends BaseEntity {
    private Long merchantId;
    private LocalDate settlementDate;
    private BigDecimal originalAmount;
    private BigDecimal feeAmount;
    private BigDecimal feeRate;
    private BigDecimal settlementAmount;

    @Enumerated(EnumType.STRING)
    private SettlementState state;

    public SettlementEntity(
            Long merchantId,
            LocalDate settlementDate,
            BigDecimal originalAmount,
            BigDecimal feeAmount,
            BigDecimal feeRate,
            BigDecimal settlementAmount,
            SettlementState state
    ) {
        this.merchantId = merchantId;
        this.settlementDate = settlementDate;
        this.originalAmount = originalAmount;
        this.feeAmount = feeAmount;
        this.feeRate = feeRate;
        this.settlementAmount = settlementAmount;
        this.state = state;
    }

    public void sent() {
        this.state = SettlementState.SENT;
    }
}
