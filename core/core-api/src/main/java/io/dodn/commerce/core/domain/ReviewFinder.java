package io.dodn.commerce.core.domain;

import io.dodn.commerce.core.enums.EntityStatus;
import io.dodn.commerce.core.support.OffsetLimit;
import io.dodn.commerce.core.support.Page;
import io.dodn.commerce.storage.db.core.ReviewEntity;
import io.dodn.commerce.storage.db.core.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ReviewFinder {
    private final ReviewRepository reviewRepository;

    public Page<Review> find(ReviewTarget target, OffsetLimit offsetLimit) {
        Slice<ReviewEntity> result = reviewRepository.findByTargetTypeAndTargetIdAndStatus(
                target.type(),
                target.id(),
                EntityStatus.ACTIVE,
                offsetLimit.toPageable()
        );

        return new Page<>(
                result.getContent().stream()
                        .map(it -> new Review(
                                it.getId(),
                                it.getUserId(),
                                new ReviewTarget(it.getTargetType(), it.getTargetId()),
                                new ReviewContent(it.getRate(), it.getContent())
                        ))
                        .toList(),
                result.hasNext()
        );
    }

    public RateSummary findRateSummary(ReviewTarget target) {
        List<ReviewEntity> founds = reviewRepository.findByTargetTypeAndTargetId(target.type(), target.id()).stream()
                .filter(ReviewEntity::isActive)
                .toList();

        if (founds.isEmpty()) {
            return RateSummary.EMPTY;
        } else {
            BigDecimal sum = founds.stream()
                    .map(ReviewEntity::getRate)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal average = sum.divide(BigDecimal.valueOf(founds.size()), 2, RoundingMode.HALF_UP);

            return new RateSummary(average, (long) founds.size());
        }
    }
}
