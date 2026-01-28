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

@Getter
@Entity
@Table(
        name = "owned_coupon",
        indexes = {
                @Index(name = "udx_owned_coupon", columnList = "userId, couponId", unique = true)
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OwnedCouponEntity extends BaseEntity {
    private Long userId;
    private Long couponId;

    @Enumerated(EnumType.STRING)
    private OwnedCouponState state;

    @Version
    private Long version;

    public static OwnedCouponEntity create(Long userId, Long couponId, OwnedCouponState state) {
        OwnedCouponEntity ownedCoupon = new OwnedCouponEntity();
        ownedCoupon.userId = userId;
        ownedCoupon.couponId = couponId;
        ownedCoupon.state = state;
        ownedCoupon.version = 0L;

        return ownedCoupon;
    }

    public void use() {
        this.state = OwnedCouponState.USED;
    }

    public void revert() {
        this.state = OwnedCouponState.DOWNLOADED;
    }
}
