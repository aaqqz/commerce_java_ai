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

@Getter
@Entity
@Table(
        name = "settlement",
        indexes = {
                @Index(name = "udx_settlement_merchant", columnList = "settlementDate, merchantId", unique = true)
        }
)
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

    public static SettlementEntity create(
            Long merchantId,
            LocalDate settlementDate,
            BigDecimal originalAmount,
            BigDecimal feeAmount,
            BigDecimal feeRate,
            BigDecimal settlementAmount,
            SettlementState state
    ) {
        SettlementEntity settlement = new SettlementEntity();
        settlement.merchantId = merchantId;
        settlement.settlementDate = settlementDate;
        settlement.originalAmount = originalAmount;
        settlement.feeAmount = feeAmount;
        settlement.feeRate = feeRate;
        settlement.settlementAmount = settlementAmount;
        settlement.state = state;

        return settlement;
    }

    public void sent() {
        this.state = SettlementState.SENT;
    }
}
