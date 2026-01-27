package io.dodn.commerce.core.domain;

import io.dodn.commerce.core.enums.TransactionType;
import io.dodn.commerce.storage.db.core.MerchantProductMappingEntity;
import io.dodn.commerce.storage.db.core.MerchantProductMappingRepository;
import io.dodn.commerce.storage.db.core.OrderItemEntity;
import io.dodn.commerce.storage.db.core.OrderItemRepository;
import io.dodn.commerce.storage.db.core.SettlementTargetEntity;
import io.dodn.commerce.storage.db.core.SettlementTargetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SettlementTargetLoader {
    private final SettlementTargetRepository settlementTargetRepository;
    private final OrderItemRepository orderItemRepository;
    private final MerchantProductMappingRepository merchantProductMappingRepository;

    @Transactional
    public void process(LocalDate settleDate, TransactionType transactionType, Map<Long, Long> transactionIdMap) {
        List<OrderItemEntity> orderItems = orderItemRepository.findByOrderIdIn(transactionIdMap.keySet());

        Map<Long, MerchantProductMappingEntity> merchantMappingMap =
                merchantProductMappingRepository.findByProductIdIn(
                                orderItems.stream().map(OrderItemEntity::getProductId).collect(Collectors.toSet())
                        ).stream()
                        .collect(Collectors.toMap(MerchantProductMappingEntity::getProductId, m -> m));

        List<SettlementTargetEntity> targets = orderItems.stream()
                .map(item -> {
                    MerchantProductMappingEntity mapping = merchantMappingMap.get(item.getProductId());
                    if (mapping == null) {
                        throw new IllegalStateException("상품 " + item.getProductId() + " 의 가맹점 매핑이 존재하지 않음");
                    }
                    Long transactionId = transactionIdMap.get(item.getOrderId());
                    if (transactionId == null) {
                        throw new IllegalStateException("주문 " + item.getOrderId() + " 의 거래 ID 매핑이 존재하지 않음");
                    }

                    return new SettlementTargetEntity(
                            settleDate,
                            mapping.getMerchantId(),
                            transactionType,
                            transactionId,
                            item.getOrderId(),
                            item.getProductId(),
                            item.getQuantity(),
                            item.getUnitPrice(),
                            item.getTotalPrice(),
                            transactionType == TransactionType.PAYMENT
                                    ? item.getTotalPrice()
                                    : item.getTotalPrice().negate()
                    );
                })
                .collect(Collectors.toList());

        settlementTargetRepository.saveAll(targets);
    }
}
