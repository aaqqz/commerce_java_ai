package io.dodn.commerce.core.domain;

import io.dodn.commerce.storage.db.core.BrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BrandFinder {
    private final BrandRepository brandRepository;

    public List<Brand> findByIds(List<Long> brandIds) {
        if (brandIds.isEmpty()) {
            return List.of();
        }
        return brandRepository.findAllById(brandIds).stream()
                .map(it -> new Brand(
                        it.getId(),
                        it.getName()
                ))
                .toList();
    }
}
