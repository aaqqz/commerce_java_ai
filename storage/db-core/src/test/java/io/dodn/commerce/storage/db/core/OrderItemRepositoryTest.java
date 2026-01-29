package io.dodn.commerce.storage.db.core;

import io.dodn.commerce.core.enums.EntityStatus;
import io.dodn.commerce.core.enums.OrderState;
import io.dodn.commerce.storage.db.CoreDbContextTest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class OrderItemRepositoryTest extends CoreDbContextTest {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public OrderItemRepositoryTest(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @Test
    void 조건에_맞는_주문상품만_조회_되어야한다() {
        // given
        Long userId = 100L;
        Long productId = 10L;

        // 포함 대상: 해당 유저, PAID, ACTIVE, fromDate 이후, 해당 상품
        OrderEntity includedOrder = orderRepository.save(
                OrderEntity.create(
                        userId,
                        "ORDER_KEY_INCLUDED",
                        "Included Order",
                        new BigDecimal(1000),
                        OrderState.PAID
                )
        );
        OrderItemEntity includedItem = orderItemRepository.save(
                OrderItemEntity.create(
                        includedOrder.getId(),
                        productId,
                        "Prod",
                        "http://example.com/thumb.jpg",
                        "desc",
                        1L,
                        new BigDecimal(1000),
                        new BigDecimal(1000)
                )
        );

        // 동일 조건이지만 다른 유저
        OrderEntity otherUserOrder = orderRepository.save(
                OrderEntity.create(
                        200L,
                        "ORDER_KEY_OTHER_USER",
                        "Other User Order",
                        new BigDecimal(1000),
                        OrderState.PAID
                )
        );
        orderItemRepository.save(
                OrderItemEntity.create(
                        otherUserOrder.getId(),
                        productId,
                        "Prod",
                        "http://example.com/thumb.jpg",
                        "desc",
                        1L,
                        new BigDecimal(1000),
                        new BigDecimal(1000)
                )
        );

        // 동일 유저지만 CREATED 상태
        OrderEntity createdOrder = orderRepository.save(
                OrderEntity.create(
                        userId,
                        "ORDER_KEY_CREATED",
                        "Created Order",
                        new BigDecimal(1000),
                        OrderState.CREATED
                )
        );
        orderItemRepository.save(
                OrderItemEntity.create(
                        createdOrder.getId(),
                        productId,
                        "Prod",
                        "http://example.com/thumb.jpg",
                        "desc",
                        1L,
                        new BigDecimal(1000),
                        new BigDecimal(1000)
                )
        );

        // 동일 유저, PAID지만 주문이 삭제됨
        OrderEntity deletedOrder = orderRepository.save(
                OrderEntity.create(
                        userId,
                        "ORDER_KEY_DELETED",
                        "Deleted Order",
                        new BigDecimal(1000),
                        OrderState.PAID
                )
        );
        deletedOrder.delete();
        orderRepository.save(deletedOrder);
        orderItemRepository.save(
                OrderItemEntity.create(
                        deletedOrder.getId(),
                        productId,
                        "Prod",
                        "http://example.com/thumb.jpg",
                        "desc",
                        1L,
                        new BigDecimal(1000),
                        new BigDecimal(1000)
                )
        );

        // 동일 유저, PAID지만 아이템이 삭제됨
        OrderEntity orderForDeletedItem = orderRepository.save(
                OrderEntity.create(
                        userId,
                        "ORDER_KEY_ITEM_DELETED",
                        "Order For Deleted Item",
                        new BigDecimal(1000),
                        OrderState.PAID
                )
        );
        OrderItemEntity deletedItem = orderItemRepository.save(
                OrderItemEntity.create(
                        orderForDeletedItem.getId(),
                        productId,
                        "Prod",
                        "http://example.com/thumb.jpg",
                        "desc",
                        1L,
                        new BigDecimal(1000),
                        new BigDecimal(1000)
                )
        );
        deletedItem.delete();
        orderItemRepository.save(deletedItem);

        // 동일 유저, PAID, ACTIVE지만 다른 상품
        OrderEntity orderOtherProduct = orderRepository.save(
                OrderEntity.create(
                        userId,
                        "ORDER_KEY_OTHER_PRODUCT",
                        "Order Other Product",
                        new BigDecimal(1000),
                        OrderState.PAID
                )
        );
        orderItemRepository.save(
                OrderItemEntity.create(
                        orderOtherProduct.getId(),
                        999L,
                        "Other Prod",
                        "http://example.com/thumb2.jpg",
                        "desc2",
                        1L,
                        new BigDecimal(2000),
                        new BigDecimal(2000)
                )
        );

        // when
        // fromDate는 포함 대상 주문의 생성시간 직전으로 설정하여 포함되도록 함
        LocalDateTime fromDate = includedOrder.getCreatedAt().minusSeconds(1);
        List<OrderItemEntity> result = orderItemRepository.findRecentOrderItemsForProduct(
                userId,
                productId,
                OrderState.PAID,
                fromDate,
                EntityStatus.ACTIVE
        );

        // then
        List<Long> resultIds = result.stream().map(OrderItemEntity::getId).toList();
        assertThat(resultIds).containsExactly(includedItem.getId());
    }
}
