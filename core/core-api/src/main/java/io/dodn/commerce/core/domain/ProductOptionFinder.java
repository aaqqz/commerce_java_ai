package io.dodn.commerce.core.domain;

import io.dodn.commerce.storage.db.core.BaseEntity;
import io.dodn.commerce.storage.db.core.ProductOptionEntity;
import io.dodn.commerce.storage.db.core.ProductOptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductOptionFinder {
    private final ProductOptionRepository productOptionRepository;

    public List<ProductOption> find(Long productId) {
        return productOptionRepository.findByProductId(productId).stream()
                .filter(BaseEntity::isActive)
                .sorted(Comparator.comparing(ProductOptionEntity::getPriority))
                .map(it -> new ProductOption(
                        it.getId(),
                        it.getProductId(),
                        it.getName(),
                        it.getDescription(),
                        new Price(
                                it.getCostPrice(),
                                it.getSalesPrice(),
                                it.getDiscountedPrice()
                        )
                ))
                .toList();
    }
}
