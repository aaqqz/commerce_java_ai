package io.dodn.commerce.core.domain;

import io.dodn.commerce.core.enums.EntityStatus;
import io.dodn.commerce.storage.db.core.MerchantEntity;
import io.dodn.commerce.storage.db.core.MerchantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MerchantFinder {
    private final MerchantRepository merchantRepository;

    public List<Merchant> findByIds(List<Long> merchantIds) {
        if (merchantIds.isEmpty()) {
            return List.of();
        }
        return merchantRepository.findAllById(merchantIds).stream()
                .map(entity -> new Merchant(
                        entity.getId(),
                        entity.getName(),
                        entity.getSettlementCycle()
                ))
                .toList();
    }
}
