package io.dodn.commerce.core.domain;

import io.dodn.commerce.core.enums.EntityStatus;
import io.dodn.commerce.core.support.OffsetLimit;
import io.dodn.commerce.core.support.Page;
import io.dodn.commerce.storage.db.core.ReviewEntity;
import io.dodn.commerce.storage.db.core.ReviewImageEntity;
import io.dodn.commerce.storage.db.core.ReviewImageRepository;
import io.dodn.commerce.storage.db.core.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ReviewFinder {
    private final ReviewRepository reviewRepository;
    private final ReviewImageRepository reviewImageRepository;

    public Page<Review> find(ReviewTarget target, OffsetLimit offsetLimit, Boolean imageOnly) {
        Slice<ReviewEntity> result;

        if (imageOnly) {
            result = reviewRepository.findImageReviewsByTargetTypeAndTargetIdAndStatus(
                    target.type(),
                    target.id(),
                    EntityStatus.ACTIVE,
                    offsetLimit.toPageable()
            );
        } else {
            result = reviewRepository.findByTargetTypeAndTargetIdAndStatus(
                    target.type(),
                    target.id(),
                    EntityStatus.ACTIVE,
                    offsetLimit.toPageable()
            );
        }

        List<Long> reviewIds = result.getContent().stream()
                .map(ReviewEntity::getId)
                .toList();

        Map<Long, List<ReviewImage>> imageMap;
        if (reviewIds.isEmpty()) {
            imageMap = Map.of();
        } else {
            var reviewImageEntities = reviewImageRepository.findByReviewIdInAndStatus(reviewIds, EntityStatus.ACTIVE);

            imageMap = reviewImageEntities.stream()
                    .collect(Collectors.groupingBy(
                            ReviewImageEntity::getReviewId,
                            Collectors.collectingAndThen(
                                    Collectors.mapping(
                                            entity -> new ReviewImage(
                                                    entity.getId(),
                                                    entity.getImageUrl()
                                            ),
                                            Collectors.toList()
                                    ),
                                    list -> list.stream()
                                            .sorted(Comparator.comparing(ReviewImage::id))
                                            .toList()
                            )
                    ));
        }

        var reviews = result.getContent().stream()
                .map(it ->
                        new Review(
                                it.getId(),
                                it.getUserId(),
                                new ReviewTarget(it.getTargetType(), it.getTargetId()),
                                new ReviewContent(it.getRate(), it.getContent()),
                                imageMap.getOrDefault(it.getId(), List.of())
                ))
                .toList();

        return new Page<>(
                reviews,
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
