package io.dodn.commerce.storage.db.core;

import io.dodn.commerce.core.enums.EntityStatus;
import io.dodn.commerce.core.enums.OrderState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItemEntity, Long> {
    List<OrderItemEntity> findByOrderId(Long orderId);
    List<OrderItemEntity> findByOrderIdIn(Collection<Long> orderId);

    @Query("""
        SELECT item FROM OrderItemEntity item
            JOIN OrderEntity orderEntity ON item.orderId = orderEntity.id
        WHERE orderEntity.userId = :userId
            AND orderEntity.state = :state
            AND orderEntity.status = :status
            AND orderEntity.createdAt >= :fromDate
            AND item.productId = :productId
            AND item.status = :status
        """)
    List<OrderItemEntity> findRecentOrderItemsForProduct(
            Long userId,
            Long productId,
            OrderState state,
            LocalDateTime fromDate,
            EntityStatus status
    );

    @Query("""
        SELECT item.productId as targetId, COUNT(distinct orderEntity.id) as count
        FROM OrderItemEntity item
            JOIN OrderEntity orderEntity ON item.orderId = orderEntity.id
        WHERE item.productId IN :productIds
            AND orderEntity.state = :state
            AND orderEntity.status = :status
            AND orderEntity.createdAt >= :from
            AND item.status = :status
        GROUP BY item.productId
        """)
    List<TargetCountProjection> countOrdersByProductIdsAndStateAndCreatedAtAfter(
        Collection<Long> productIds,
        OrderState state,
        LocalDateTime from,
        EntityStatus status
    );
}
