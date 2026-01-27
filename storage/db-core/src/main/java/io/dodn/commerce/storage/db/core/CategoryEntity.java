package io.dodn.commerce.storage.db.core;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "category")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CategoryEntity extends BaseEntity {
    private Long categoryId;
    private Long categoryName;

    public CategoryEntity(Long categoryId, Long categoryName) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }
}
