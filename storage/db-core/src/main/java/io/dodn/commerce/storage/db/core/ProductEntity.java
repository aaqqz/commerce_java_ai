package io.dodn.commerce.storage.db.core;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "product")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductEntity extends BaseEntity {
    private String name;
    private String thumbnailUrl;
    private String description;
    private String shortDescription;
    private BigDecimal costPrice;
    private BigDecimal salesPrice;
    private BigDecimal discountedPrice;

    public ProductEntity(
            String name,
            String thumbnailUrl,
            String description,
            String shortDescription,
            BigDecimal costPrice,
            BigDecimal salesPrice,
            BigDecimal discountedPrice
    ) {
        this.name = name;
        this.thumbnailUrl = thumbnailUrl;
        this.description = description;
        this.shortDescription = shortDescription;
        this.costPrice = costPrice;
        this.salesPrice = salesPrice;
        this.discountedPrice = discountedPrice;
    }
}
