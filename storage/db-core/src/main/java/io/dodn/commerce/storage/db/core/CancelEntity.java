package io.dodn.commerce.storage.db.core;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "cancel")
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

    public static CancelEntity create(
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
        CancelEntity cancel = new CancelEntity();
        cancel.userId = userId;
        cancel.orderId = orderId;
        cancel.paymentId = paymentId;
        cancel.originAmount = originAmount;
        cancel.ownedCouponId = ownedCouponId;
        cancel.couponDiscount = couponDiscount;
        cancel.usedPoint = usedPoint;
        cancel.paidAmount = paidAmount;
        cancel.canceledAmount = canceledAmount;
        cancel.externalCancelKey = externalCancelKey;
        cancel.canceledAt = canceledAt;

        return cancel;
    }
}
