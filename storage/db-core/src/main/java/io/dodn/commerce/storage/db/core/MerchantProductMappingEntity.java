package io.dodn.commerce.storage.db.core;

import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
        name = "merchant_product_mapping",
        indexes = {
                @Index(name = "udx_merchant_product", columnList = "merchantId, productId", unique = true)
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MerchantProductMappingEntity extends BaseEntity {
    private Long productId;
    private Long merchantId;

}
