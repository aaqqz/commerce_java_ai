package io.dodn.commerce.storage.db.core;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Entity
@Table(name = "product")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductEntity extends BaseEntity {
    private String name;
    private String thumbnailUrl;
    private String description;
    private String shortDescription;
    private BigDecimal costPrice;
    private BigDecimal salesPrice;
    private BigDecimal discountedPrice;

    public static ProductEntity create(
            String name,
            String thumbnailUrl,
            String description,
            String shortDescription,
            BigDecimal costPrice,
            BigDecimal salesPrice,
            BigDecimal discountedPrice
    ) {
        ProductEntity product = new ProductEntity();
        product.name = name;
        product.thumbnailUrl = thumbnailUrl;
        product.description = description;
        product.shortDescription = shortDescription;
        product.costPrice = costPrice;
        product.salesPrice = salesPrice;
        product.discountedPrice = discountedPrice;

        return product;
    }
}
