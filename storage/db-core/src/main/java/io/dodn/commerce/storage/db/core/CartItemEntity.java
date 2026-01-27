package io.dodn.commerce.storage.db.core;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "cart_item")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartItemEntity extends BaseEntity {
    private Long userId;
    private Long productId;
    private Long quantity;

    public static CartItemEntity create(Long userId, Long productId, Long quantity) {
        CartItemEntity cartItem = new CartItemEntity();
        cartItem.userId = userId;
        cartItem.productId = productId;
        cartItem.quantity = quantity;

        return cartItem;
    }

    public void applyQuantity(Long value) {
        this.quantity = value < 1 ? 1 : value;
    }
}
