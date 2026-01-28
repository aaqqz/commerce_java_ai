package io.dodn.commerce.storage.db.core;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Entity
@Table(name = "point_balance")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PointBalanceEntity extends BaseEntity {
    private Long userId;
    private BigDecimal balance;

    @Version
    private Long version;

    public static PointBalanceEntity create(Long userId, BigDecimal balance) {
        PointBalanceEntity pointBalance = new PointBalanceEntity();
        pointBalance.userId = userId;
        pointBalance.balance = balance;
        pointBalance.version = 0L;

        return pointBalance;
    }

    public void apply(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }
}
