package io.dodn.commerce.storage.db.core;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product_category")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductCategoryEntity extends BaseEntity {
    private Long productId;
    private Long categoryId;

    public ProductCategoryEntity(Long productId, Long categoryId) {
        this.productId = productId;
        this.categoryId = categoryId;
    }
}
