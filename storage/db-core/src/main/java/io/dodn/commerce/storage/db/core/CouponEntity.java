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

    private BigDecimal maxDiscountAmount;
    private BigDecimal minOrderAmount;

    @Column(nullable = false)
    private Integer usesPerDownload = 1;

    public static CouponEntity create(String name, CouponType type, BigDecimal discount, LocalDateTime expiredAt) {
        CouponEntity coupon = new CouponEntity();
        coupon.name = name;
        coupon.type = type;
        coupon.discount = discount;
        coupon.expiredAt = expiredAt;
        coupon.usesPerDownload = 1;

        return coupon;
    }

    public static CouponEntity create(String name, CouponType type, BigDecimal discount, LocalDateTime expiredAt,
                                      BigDecimal maxDiscountAmount, BigDecimal minOrderAmount, Integer usesPerDownload) {
        CouponEntity coupon = new CouponEntity();
        coupon.name = name;
        coupon.type = type;
        coupon.discount = discount;
        coupon.expiredAt = expiredAt;
        coupon.maxDiscountAmount = maxDiscountAmount;
        coupon.minOrderAmount = minOrderAmount;
        coupon.usesPerDownload = usesPerDownload != null ? usesPerDownload : 1;

        return coupon;
    }
}
