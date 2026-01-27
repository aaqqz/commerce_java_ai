package io.dodn.commerce.storage.db.core;

import io.dodn.commerce.core.enums.CouponTargetType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "coupon_target")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CouponTargetEntity extends BaseEntity {
    private Long couponId;

    @Enumerated(EnumType.STRING)
    private CouponTargetType targetType;

    private Long targetId;

    public CouponTargetEntity(Long couponId, CouponTargetType targetType, Long targetId) {
        this.couponId = couponId;
        this.targetType = targetType;
        this.targetId = targetId;
    }
}
