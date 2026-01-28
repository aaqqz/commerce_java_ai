package io.dodn.commerce.storage.db.core;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "product_category")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductCategoryEntity extends BaseEntity {
    private Long productId;
    private Long categoryId;

    public static ProductCategoryEntity create(Long productId, Long categoryId) {
        ProductCategoryEntity category = new ProductCategoryEntity();
        category.productId = productId;
        category.categoryId = categoryId;

        return category;
    }
}
