package io.dodn.commerce.storage.db.core;

import io.dodn.commerce.core.enums.PaymentMethod;
import io.dodn.commerce.core.enums.PaymentState;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(
        name = "payment",
        indexes = {
                @Index(name = "udx_order_id", columnList = "orderId", unique = true)
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentEntity extends BaseEntity {
    private Long userId;
    private Long orderId;
    private BigDecimal originAmount;
    private Long ownedCouponId;
    private BigDecimal couponDiscount;
    private BigDecimal usedPoint;
    private BigDecimal paidAmount;

    @Enumerated(EnumType.STRING)
    private PaymentState state;

    private String externalPaymentKey;

    @Enumerated(EnumType.STRING)
    private PaymentMethod method;

    private String approveCode;
    private LocalDateTime paidAt;

    public static PaymentEntity create(
            Long userId,
            Long orderId,
            BigDecimal originAmount,
            Long ownedCouponId,
            BigDecimal couponDiscount,
            BigDecimal usedPoint,
            BigDecimal paidAmount,
            PaymentState state
    ) {
        PaymentEntity payment = new PaymentEntity();
        payment.userId = userId;
        payment.orderId = orderId;
        payment.originAmount = originAmount;
        payment.ownedCouponId = ownedCouponId;
        payment.couponDiscount = couponDiscount;
        payment.usedPoint = usedPoint;
        payment.paidAmount = paidAmount;
        payment.state = state;

        payment.externalPaymentKey = null;
        payment.method = null;
        payment.approveCode = null;
        payment.paidAt = null;

        return payment;
    }

    public void success(String externalPaymentKey, PaymentMethod method, String approveCode) {
        this.state = PaymentState.SUCCESS;
        this.externalPaymentKey = externalPaymentKey;
        this.method = method;
        this.approveCode = approveCode;
        this.paidAt = LocalDateTime.now();
    }

    public boolean hasAppliedCoupon() {
        return ownedCouponId > 0;
    }
}
