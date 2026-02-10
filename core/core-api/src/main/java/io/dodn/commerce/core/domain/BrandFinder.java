package io.dodn.commerce.core.domain;

import io.dodn.commerce.core.enums.EntityStatus;
import io.dodn.commerce.storage.db.core.BrandEntity;
import io.dodn.commerce.storage.db.core.BrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BrandFinder {
    private final BrandRepository brandRepository;

    public List<Brand> find(List<Long> brandIds) {
        return brandRepository.findByIdInAndStatus(brandIds, EntityStatus.ACTIVE)
                .stream()
                .map(entity -> new Brand(
                        entity.getId(),
                        entity.getName(),
                        entity.getLogoUrl(),
                        entity.getDescription()
                ))
                .toList();
    }
}
