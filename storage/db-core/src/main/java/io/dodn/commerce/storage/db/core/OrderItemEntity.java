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

    public OrderItemEntity(
            Long orderId,
            Long productId,
            String productName,
            String thumbnailUrl,
            String shortDescription,
            Long quantity,
            BigDecimal unitPrice,
            BigDecimal totalPrice
    ) {
        this.orderId = orderId;
        this.productId = productId;
        this.productName = productName;
        this.thumbnailUrl = thumbnailUrl;
        this.shortDescription = shortDescription;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
    }
}
