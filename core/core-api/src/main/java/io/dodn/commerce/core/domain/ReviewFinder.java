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
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ReviewFinder {
    private final ReviewRepository reviewRepository;

    public Page<Review> find(ReviewTarget target, OffsetLimit offsetLimit) {
        Slice<ReviewEntity> result = reviewRepository.findByTargetTypeAndTargetIdAndStatus(
                target.getType(),
                target.getId(),
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
                        .collect(Collectors.toList()),
                result.hasNext()
        );
    }

    public RateSummary findRateSummary(ReviewTarget target) {
        List<ReviewEntity> founds = reviewRepository.findByTargetTypeAndTargetId(target.getType(), target.getId())
                .stream()
                .filter(ReviewEntity::isActive)
                .collect(Collectors.toList());

        if (founds.isEmpty()) {
            return RateSummary.EMPTY;
        } else {
            BigDecimal sum = founds.stream()
                    .map(ReviewEntity::getRate)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal average = sum.divide(BigDecimal.valueOf(founds.size()), 2, java.math.RoundingMode.HALF_UP);
            return new RateSummary(average, (long) founds.size());
        }
    }
}
