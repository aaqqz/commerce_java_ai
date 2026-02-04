package io.dodn.commerce.core.domain;

import io.dodn.commerce.core.enums.EntityStatus;
import io.dodn.commerce.core.enums.OrderState;
import io.dodn.commerce.core.support.error.CoreException;
import io.dodn.commerce.core.support.error.ErrorType;
import io.dodn.commerce.storage.db.core.OrderEntity;
import io.dodn.commerce.storage.db.core.OrderItemEntity;
import io.dodn.commerce.storage.db.core.OrderItemRepository;
import io.dodn.commerce.storage.db.core.OrderRepository;
import io.dodn.commerce.storage.db.core.ProductEntity;
import io.dodn.commerce.storage.db.core.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderKeyGenerator orderKeyGenerator;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final OrderFinder orderFinder;

    @Transactional
    public String create(User user, NewOrder newOrder) {
        Set<Long> orderProductIds = newOrder.items().stream()
                .map(NewOrderItem::productId)
                .collect(Collectors.toSet());

        Map<Long, ProductEntity> productMap = productRepository
                .findByIdInAndStatus(orderProductIds, EntityStatus.ACTIVE)
                .stream()
                .collect(Collectors.toMap(ProductEntity::getId, p -> p));

        if (productMap.isEmpty()) {
            throw new CoreException(ErrorType.NOT_FOUND_DATA);
        }
        if (!productMap.keySet().equals(orderProductIds)) {
            throw new CoreException(ErrorType.PRODUCT_MISMATCH_IN_ORDER);
        }

        NewOrderItem firstItem = newOrder.items().get(0);
        ProductEntity firstProduct = productMap.get(firstItem.productId());
        String orderName = firstProduct.getName() +
                (newOrder.items().size() > 1 ? " 외 " + (newOrder.items().size() - 1) + "개" : "");

        BigDecimal totalPrice = newOrder.items().stream()
                .map(item -> {
                    ProductEntity product = productMap.get(item.productId());
                    return product.getDiscountedPrice().multiply(BigDecimal.valueOf(item.quantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        OrderEntity order = OrderEntity.create(
                user.id(),
                orderKeyGenerator.generate(),
                orderName,
                totalPrice,
                OrderState.CREATED
        );
        OrderEntity savedOrder = orderRepository.save(order);

        List<OrderItemEntity> orderItems = newOrder.items().stream()
                .map(item -> {
                    ProductEntity product = productMap.get(item.productId());
                    return OrderItemEntity.create(
                            savedOrder.getId(),
                            product.getId(),
                            product.getName(),
                            product.getThumbnailUrl(),
                            product.getShortDescription(),
                            item.quantity(),
                            product.getDiscountedPrice(),
                            product.getDiscountedPrice().multiply(BigDecimal.valueOf(item.quantity()))
                    );
                })
                .toList();

        orderItemRepository.saveAll(orderItems);

        return savedOrder.getOrderKey();
    }

    @Transactional
    public List<OrderSummary> getOrders(User user) {
        List<OrderEntity> orders = orderRepository.findByUserIdAndStateAndStatusOrderByIdDesc(user.id(), OrderState.PAID, EntityStatus.ACTIVE);
        if (orders.isEmpty()) {
            return List.of();
        }

        return orders.stream()
                .map(it -> new OrderSummary(
                        it.getId(),
                        it.getOrderKey(),
                        it.getName(),
                        user.id(),
                        it.getTotalPrice(),
                        it.getState()
                ))
                .toList();
    }

    @Transactional
    public Order getOrder(User user, String orderKey, OrderState state) {
        OrderEntity order = orderRepository.findByOrderKeyAndStateAndStatus(orderKey, state, EntityStatus.ACTIVE)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND_DATA));

        if (!order.getUserId().equals(user.id())) {
            throw new CoreException(ErrorType.NOT_FOUND_DATA);
        }

        List<OrderItemEntity> items = orderItemRepository.findByOrderId(order.getId());
        if (items.isEmpty()) {
            throw new CoreException(ErrorType.NOT_FOUND_DATA);
        }

        return new Order(
                order.getId(),
                order.getOrderKey(),
                order.getName(),
                user.id(),
                order.getTotalPrice(),
                order.getState(),
                items.stream()
                        .map(it -> new OrderItem(
                                order.getId(),
                                it.getProductId(),
                                it.getProductName(),
                                it.getThumbnailUrl(),
                                it.getShortDescription(),
                                it.getQuantity(),
                                it.getUnitPrice(),
                                it.getTotalPrice()
                        ))
                        .toList()
        );
    }

    public Map<Long, Long> recentCount(List<Long> productIds, LocalDateTime from) {
        return orderFinder.countOrdersByProductIds(productIds, from);
    }
}
