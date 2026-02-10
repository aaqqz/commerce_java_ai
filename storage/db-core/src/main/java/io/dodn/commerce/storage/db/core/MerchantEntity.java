package io.dodn.commerce.storage.db.core;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "merchant")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MerchantEntity extends BaseEntity {
    @Column(nullable = false)
    private String name;

    private Integer settlementCycle = 1;
}
