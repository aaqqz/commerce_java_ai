package io.dodn.commerce.core.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductSectionService {
    private final ProductFinder productFinder;

    public List<ProductSection> findSections(Long productId) {
        return productFinder.findSections(productId);
    }
}
