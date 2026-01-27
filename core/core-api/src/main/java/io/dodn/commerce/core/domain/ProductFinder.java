package io.dodn.commerce.core.domain;

import io.dodn.commerce.core.enums.EntityStatus;
import io.dodn.commerce.core.support.OffsetLimit;
import io.dodn.commerce.core.support.Page;
import io.dodn.commerce.core.support.error.CoreException;
import io.dodn.commerce.core.support.error.ErrorType;
import io.dodn.commerce.storage.db.core.ProductCategoryRepository;
import io.dodn.commerce.storage.db.core.ProductEntity;
import io.dodn.commerce.storage.db.core.ProductRepository;
import io.dodn.commerce.storage.db.core.ProductSectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductFinder {
    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final ProductSectionRepository productSectionRepository;

    public Page<Product> findByCategory(Long categoryId, OffsetLimit offsetLimit) {
        Slice<io.dodn.commerce.storage.db.core.ProductCategoryEntity> categories =
                productCategoryRepository.findByCategoryIdAndStatus(categoryId, EntityStatus.ACTIVE, offsetLimit.toPageable());

        List<Product> products = productRepository.findAllById(
                        categories.getContent().stream()
                                .map(io.dodn.commerce.storage.db.core.ProductCategoryEntity::getProductId)
                                .collect(Collectors.toList())
                ).stream()
                .map(it -> new Product(
                        it.getId(),
                        it.getName(),
                        it.getThumbnailUrl(),
                        it.getDescription(),
                        it.getShortDescription(),
                        new Price(
                                it.getCostPrice(),
                                it.getSalesPrice(),
                                it.getDiscountedPrice()
                        )
                ))
                .collect(Collectors.toList());

        return new Page<>(products, categories.hasNext());
    }

    public Product find(Long productId) {
        ProductEntity found = productRepository.findById(productId)
                .filter(ProductEntity::isActive)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND_DATA));

        return new Product(
                found.getId(),
                found.getName(),
                found.getThumbnailUrl(),
                found.getDescription(),
                found.getShortDescription(),
                new Price(
                        found.getCostPrice(),
                        found.getSalesPrice(),
                        found.getDiscountedPrice()
                )
        );
    }

    public List<ProductSection> findSections(Long productId) {
        return productSectionRepository.findByProductId(productId).stream()
                .filter(io.dodn.commerce.storage.db.core.ProductSectionEntity::isActive)
                .map(it -> new ProductSection(it.getType(), it.getContent()))
                .collect(Collectors.toList());
    }
}
