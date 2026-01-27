package io.dodn.commerce.storage.db.core;

import io.dodn.commerce.core.enums.CouponType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "coupon")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CouponEntity extends BaseEntity {
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(50)")
    private CouponType type;

    private BigDecimal discount;
    private LocalDateTime expiredAt;

    public static CouponEntity create(String name, CouponType type, BigDecimal discount, LocalDateTime expiredAt) {
        CouponEntity coupon = new CouponEntity();
        coupon.name = name;
        coupon.type = type;
        coupon.discount = discount;
        coupon.expiredAt = expiredAt;

        return coupon;
    }
}
