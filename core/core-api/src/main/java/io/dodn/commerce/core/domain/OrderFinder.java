package io.dodn.commerce.core.domain;

import io.dodn.commerce.core.enums.EntityStatus;
import io.dodn.commerce.core.enums.OrderState;
import io.dodn.commerce.storage.db.core.OrderItemRepository;
import io.dodn.commerce.storage.db.core.TargetCountProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderFinder {
    private final OrderItemRepository orderItemRepository;

    public Map<Long, Long> countOrdersByProductIds(List<Long> productIds, LocalDateTime from) {
        if (productIds.isEmpty()) {
            return Map.of();
        }

        List<TargetCountProjection> results = orderItemRepository.countOrdersByProductIdsAndStateAndCreatedAtAfter(
                productIds,
                OrderState.PAID,
                from,
                EntityStatus.ACTIVE
        );

        return results.stream()
                .collect(Collectors.toMap(
                        TargetCountProjection::getProductId,
                        TargetCountProjection::getCount
                ));
    }
}
