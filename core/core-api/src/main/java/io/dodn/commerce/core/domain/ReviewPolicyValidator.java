package io.dodn.commerce.core.domain;

import io.dodn.commerce.core.enums.EntityStatus;
import io.dodn.commerce.core.enums.OrderState;
import io.dodn.commerce.core.enums.ReviewTargetType;
import io.dodn.commerce.core.support.error.CoreException;
import io.dodn.commerce.core.support.error.ErrorType;
import io.dodn.commerce.storage.db.core.OrderItemRepository;
import io.dodn.commerce.storage.db.core.ReviewEntity;
import io.dodn.commerce.storage.db.core.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ReviewPolicyValidator {
    private final OrderItemRepository orderItemRepository;
    private final ReviewRepository reviewRepository;

    public ReviewKey validateNew(User user, ReviewTarget target) {
        if (target.type() == ReviewTargetType.PRODUCT) {
            List<String> reviewKeys = orderItemRepository.findRecentOrderItemsForProduct(
                            user.id(),
                            target.id(),
                            OrderState.PAID,
                            LocalDateTime.now().minusDays(14),
                            EntityStatus.ACTIVE
                    ).stream()
                    .map(it -> "ORDER_ITEM_" + it.getId())
                    .toList();

            Set<String> existReviewKeys = reviewRepository.findByUserIdAndReviewKeyIn(user.id(), reviewKeys).stream()
                    .map(ReviewEntity::getReviewKey)
                    .collect(Collectors.toSet());

            String key = reviewKeys.stream()
                    .filter(k -> !existReviewKeys.contains(k))
                    .findFirst()
                    .orElseThrow(() -> new CoreException(ErrorType.REVIEW_HAS_NOT_ORDER));

            return new ReviewKey(user, key);
        }
        throw new UnsupportedOperationException();
    }

    public void validateUpdate(User user, Long reviewId) {
        ReviewEntity review = reviewRepository.findByIdAndUserId(reviewId, user.id())
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND_DATA));

        if (review.getCreatedAt().plusDays(7).isBefore(LocalDateTime.now())) {
            throw new CoreException(ErrorType.REVIEW_UPDATE_EXPIRED);
        }
    }
}
