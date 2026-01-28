package io.dodn.commerce.core.api.controller.v1.response;

import io.dodn.commerce.core.domain.Review;
import io.dodn.commerce.core.enums.ReviewTargetType;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public record ReviewResponse(
        Long id,
        ReviewTargetType targetType,
        Long targetId,
        BigDecimal rate,
        String content
) {
    public static ReviewResponse of(Review review) {
        return new ReviewResponse(
                review.getId(),
                review.getTarget().type(),
                review.getTarget().id(),
                review.getContent().rate(),
                review.getContent().content()
        );
    }

    public static List<ReviewResponse> of(List<Review> reviews) {
        return reviews.stream()
                .map(ReviewResponse::of)
                .collect(Collectors.toList());
    }
}
