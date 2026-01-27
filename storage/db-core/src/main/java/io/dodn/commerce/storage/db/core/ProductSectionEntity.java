package io.dodn.commerce.storage.db.core;

import io.dodn.commerce.core.enums.ProductSectionType;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product_section")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductSectionEntity extends BaseEntity {
    private Long productId;
    private ProductSectionType type;
    private String content;

    public ProductSectionEntity(Long productId, ProductSectionType type, String content) {
        this.productId = productId;
        this.type = type;
        this.content = content;
    }
}
