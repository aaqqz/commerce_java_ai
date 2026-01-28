package io.dodn.commerce.storage.db.core;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Entity
@Table(name = "order_item")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItemEntity extends BaseEntity {
    private Long orderId;
    private Long productId;
    private String productName;
    private String thumbnailUrl;
    private String shortDescription;
    private Long quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;

    public static OrderItemEntity create(
            Long orderId,
            Long productId,
            String productName,
            String thumbnailUrl,
            String shortDescription,
            Long quantity,
            BigDecimal unitPrice,
            BigDecimal totalPrice
    ) {
        OrderItemEntity orderItem = new OrderItemEntity();
        orderItem.orderId = orderId;
        orderItem.productId = productId;
        orderItem.productName = productName;
        orderItem.thumbnailUrl = thumbnailUrl;
        orderItem.shortDescription = shortDescription;
        orderItem.quantity = quantity;
        orderItem.unitPrice = unitPrice;
        orderItem.totalPrice = totalPrice;

        return orderItem;
    }
}
