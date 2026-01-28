package io.dodn.commerce.storage.db.core;

import io.dodn.commerce.core.enums.ProductSectionType;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "product_section")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductSectionEntity extends BaseEntity {
    private Long productId;
    private ProductSectionType type;
    private String content;

    public static ProductSectionEntity create(Long productId, ProductSectionType type, String content) {
        ProductSectionEntity section = new ProductSectionEntity();
        section.productId = productId;
        section.type = type;
        section.content = content;

        return section;
    }
}
