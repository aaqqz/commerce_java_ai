package io.dodn.commerce.storage.db.core;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Entity
@Table(name = "product_option")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductOptionEntity extends BaseEntity {
    private Long productId;
    private String name;
    private String description;
    private BigDecimal costPrice;
    private BigDecimal salesPrice;
    private BigDecimal discountedPrice;
    private Integer priority;

    public static ProductOptionEntity create(
            Long productId,
            String name,
            String description,
            BigDecimal costPrice,
            BigDecimal salesPrice,
            BigDecimal discountedPrice,
            Integer priority
    ) {
        ProductOptionEntity option = new ProductOptionEntity();
        option.productId = productId;
        option.name = name;
        option.description = description;
        option.costPrice = costPrice;
        option.salesPrice = salesPrice;
        option.discountedPrice = discountedPrice;
        option.priority = priority;

        return option;
    }
}
