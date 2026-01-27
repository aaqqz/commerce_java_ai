package io.dodn.commerce.storage.db.core;

import io.dodn.commerce.core.enums.OrderState;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(
        name = "`order`",
        indexes = {
                @Index(name = "udx_order_key", columnList = "orderKey", unique = true)
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderEntity extends BaseEntity {
    private Long userId;
    private String orderKey;
    private String name;
    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    private OrderState state;

    public OrderEntity(Long userId, String orderKey, String name, BigDecimal totalPrice, OrderState state) {
        this.userId = userId;
        this.orderKey = orderKey;
        this.name = name;
        this.totalPrice = totalPrice;
        this.state = state;
    }

    public void paid() {
        this.state = OrderState.PAID;
    }

    public void canceled() {
        this.state = OrderState.CANCELED;
    }
}
