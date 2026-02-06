package io.dodn.commerce.core.domain;

import io.dodn.commerce.core.enums.EntityStatus;
import io.dodn.commerce.core.support.OffsetLimit;
import io.dodn.commerce.core.support.Page;
import io.dodn.commerce.core.support.error.CoreException;
import io.dodn.commerce.core.support.error.ErrorType;
import io.dodn.commerce.storage.db.core.*;
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
        Slice<ProductCategoryEntity> productCategories =
                productCategoryRepository.findByCategoryIdAndStatus(categoryId, EntityStatus.ACTIVE, offsetLimit.toPageable());

        List<Product> products = productRepository.findAllById(
                        productCategories.getContent().stream()
                                .map(ProductCategoryEntity::getProductId)
                                .toList()
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
                .toList();

        return new Page<>(products, productCategories.hasNext());
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
                .filter(ProductSectionEntity::isActive)
                .map(it -> new ProductSection(it.getType(), it.getContent()))
                .toList();
    }

    public List<Product> findByIds(List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return List.of();
        }

        return productRepository.findByIdInAndStatus(productIds, EntityStatus.ACTIVE)
                .stream()
                .map(entity -> new Product(
                        entity.getId(),
                        entity.getName(),
                        entity.getThumbnailUrl(),
                        entity.getDescription(),
                        entity.getShortDescription(),
                        new Price(
                                entity.getCostPrice(),
                                entity.getSalesPrice(),
                                entity.getDiscountedPrice()
                        )
                ))
                .toList();
    }
}
