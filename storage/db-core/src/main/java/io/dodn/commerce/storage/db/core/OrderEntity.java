package io.dodn.commerce.storage.db.core;

import io.dodn.commerce.core.enums.OrderState;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Entity
@Table(
        name = "`order`",
        indexes = {
                @Index(name = "udx_order_key", columnList = "orderKey", unique = true)
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderEntity extends BaseEntity {
    private Long userId;
    private String orderKey;
    private String name;
    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(50)")
    private OrderState state;

    public static OrderEntity create(Long userId, String orderKey, String name, BigDecimal totalPrice, OrderState state) {
        OrderEntity order = new OrderEntity();
        order.userId = userId;
        order.orderKey = orderKey;
        order.name = name;
        order.totalPrice = totalPrice;
        order.state = state;

        return order;
    }

    public void paid() {
        this.state = OrderState.PAID;
    }

    public void canceled() {
        this.state = OrderState.CANCELED;
    }
}
