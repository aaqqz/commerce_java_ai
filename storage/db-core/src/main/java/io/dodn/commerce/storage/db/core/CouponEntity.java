package io.dodn.commerce.storage.db.core;

import io.dodn.commerce.core.enums.CouponType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "coupon")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CouponEntity extends BaseEntity {
    private String name;

    @Enumerated(EnumType.STRING)
    private CouponType type;

    private BigDecimal discount;
    private LocalDateTime expiredAt;

    public CouponEntity(String name, CouponType type, BigDecimal discount, LocalDateTime expiredAt) {
        this.name = name;
        this.type = type;
        this.discount = discount;
        this.expiredAt = expiredAt;
    }
}
