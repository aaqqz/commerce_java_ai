package io.dodn.commerce.storage.db.core;

import io.dodn.commerce.core.enums.OwnedCouponState;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "owned_coupon",
        indexes = {
                @Index(name = "udx_owned_coupon", columnList = "userId, couponId", unique = true)
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OwnedCouponEntity extends BaseEntity {
    private Long userId;
    private Long couponId;

    @Enumerated(EnumType.STRING)
    private OwnedCouponState state;

    @Version
    private Long version = 0L;

    public OwnedCouponEntity(Long userId, Long couponId, OwnedCouponState state) {
        this.userId = userId;
        this.couponId = couponId;
        this.state = state;
        this.version = 0L;
    }

    public void use() {
        this.state = OwnedCouponState.USED;
    }

    public void revert() {
        this.state = OwnedCouponState.DOWNLOADED;
    }
}
