package io.dodn.commerce.storage.db.core;

import io.dodn.commerce.core.enums.CouponTargetType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "coupon_target")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CouponTargetEntity extends BaseEntity {
    private Long couponId;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(50)")
    private CouponTargetType targetType;

    private Long targetId;

    public static CouponTargetEntity create(Long couponId, CouponTargetType targetType, Long targetId) {
        CouponTargetEntity couponTarget = new CouponTargetEntity();
        couponTarget.couponId = couponId;
        couponTarget.targetType = targetType;
        couponTarget.targetId = targetId;

        return couponTarget;
    }
}
