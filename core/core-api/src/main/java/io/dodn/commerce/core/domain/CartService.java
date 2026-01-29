package io.dodn.commerce.core.domain;

import io.dodn.commerce.core.enums.EntityStatus;
import io.dodn.commerce.core.support.error.CoreException;
import io.dodn.commerce.core.support.error.ErrorType;
import io.dodn.commerce.storage.db.core.CartItemEntity;
import io.dodn.commerce.storage.db.core.CartItemRepository;
import io.dodn.commerce.storage.db.core.ProductEntity;
import io.dodn.commerce.storage.db.core.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public Cart getCart(User user) {
        List<CartItemEntity> items = cartItemRepository.findByUserIdAndStatus(user.id(), EntityStatus.ACTIVE);
        Map<Long, ProductEntity> productMap = productRepository.findAllById(
                        items.stream()
                                .map(CartItemEntity::getProductId)
                                .toList()
                ).stream()
                .collect(Collectors.toMap(ProductEntity::getId, p -> p));

        return new Cart(
                user.id(),
                items.stream()
                        .filter(it -> productMap.containsKey(it.getProductId()))
                        .map(it -> {
                            ProductEntity product = productMap.get(it.getProductId());
                            return new CartItem(
                                    it.getId(),
                                    new Product(
                                            product.getId(),
                                            product.getName(),
                                            product.getThumbnailUrl(),
                                            product.getDescription(),
                                            product.getShortDescription(),
                                            new Price(
                                                    product.getCostPrice(),
                                                    product.getSalesPrice(),
                                                    product.getDiscountedPrice()
                                            )
                                    ),
                                    it.getQuantity()
                            );
                        })
                        .toList()
        );
    }

    @Transactional
    public Long addCartItem(User user, AddCartItem item) {
        CartItemEntity found = cartItemRepository.findByUserIdAndProductId(user.id(), item.productId())
                .map(existing -> {
                    if (existing.isDeleted()) existing.active();

                    existing.applyQuantity(item.quantity());
                    return existing;
                })
                .orElseGet(() ->
                    cartItemRepository.save(
                            CartItemEntity.create(user.id(), item.productId(), item.quantity())
                    )
                );
        return found.getId();
    }

    @Transactional
    public Long modifyCartItem(User user, ModifyCartItem item) {
        CartItemEntity found = cartItemRepository.findByUserIdAndIdAndStatus(
                user.id(), item.cartItemId(), EntityStatus.ACTIVE
        ).orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND_DATA));

        found.applyQuantity(item.quantity());
        return found.getId();
    }

    @Transactional
    public void deleteCartItem(User user, Long cartItemId) {
        CartItemEntity entity = cartItemRepository.findByUserIdAndIdAndStatus(
                user.id(), cartItemId, EntityStatus.ACTIVE
        ).orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND_DATA));

        entity.delete();
    }
}
