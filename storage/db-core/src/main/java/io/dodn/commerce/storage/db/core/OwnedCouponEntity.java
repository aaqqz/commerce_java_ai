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

    private Integer totalUses;
    private Integer usedCount = 0;

    public static OwnedCouponEntity create(Long userId, Long couponId, OwnedCouponState state) {
        return create(userId, couponId, state, 1);
    }

    public static OwnedCouponEntity create(Long userId, Long couponId, OwnedCouponState state, Integer totalUses) {
        OwnedCouponEntity ownedCoupon = new OwnedCouponEntity();
        ownedCoupon.userId = userId;
        ownedCoupon.couponId = couponId;
        ownedCoupon.state = state;
        ownedCoupon.version = 0L;
        ownedCoupon.totalUses = totalUses != null ? totalUses : 1;
        ownedCoupon.usedCount = 0;

        return ownedCoupon;
    }

    // TODO: 기존 단회권 호환용 - 추후 useOne()으로 마이그레이션 예정
    public void use() {
        this.state = OwnedCouponState.USED;
    }

    // TODO: 기존 단회권 호환용 - 추후 revertOne()으로 마이그레이션 예정
    public void revert() {
        this.state = OwnedCouponState.DOWNLOADED;
    }

    public void useOne() {
        this.usedCount++;
        if (this.usedCount >= this.totalUses) {
            this.state = OwnedCouponState.EXHAUSTED;
        }
    }

    public void revertOne() {
        if (this.usedCount > 0) {
            this.usedCount--;
        }
        if (this.state == OwnedCouponState.EXHAUSTED) {
            this.state = OwnedCouponState.DOWNLOADED;
        }
    }

    public int remainingUses() {
        return this.totalUses - this.usedCount;
    }
}
