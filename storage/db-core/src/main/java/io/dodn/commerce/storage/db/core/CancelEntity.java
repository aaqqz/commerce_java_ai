package io.dodn.commerce.storage.db.core;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cancel")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CancelEntity extends BaseEntity {
    private Long userId;
    private Long orderId;
    private Long paymentId;
    private BigDecimal originAmount;
    private Long ownedCouponId;
    private BigDecimal couponDiscount;
    private BigDecimal usedPoint;
    private BigDecimal paidAmount;
    private BigDecimal canceledAmount;
    private String externalCancelKey;
    private LocalDateTime canceledAt;

    public CancelEntity(
            Long userId,
            Long orderId,
            Long paymentId,
            BigDecimal originAmount,
            Long ownedCouponId,
            BigDecimal couponDiscount,
            BigDecimal usedPoint,
            BigDecimal paidAmount,
            BigDecimal canceledAmount,
            String externalCancelKey,
            LocalDateTime canceledAt
    ) {
        this.userId = userId;
        this.orderId = orderId;
        this.paymentId = paymentId;
        this.originAmount = originAmount;
        this.ownedCouponId = ownedCouponId;
        this.couponDiscount = couponDiscount;
        this.usedPoint = usedPoint;
        this.paidAmount = paidAmount;
        this.canceledAmount = canceledAmount;
        this.externalCancelKey = externalCancelKey;
        this.canceledAt = canceledAt;
    }
}
