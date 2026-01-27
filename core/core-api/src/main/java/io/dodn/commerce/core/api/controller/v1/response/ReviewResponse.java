package io.dodn.commerce.core.api.controller.v1.response;

import io.dodn.commerce.core.domain.Review;
import io.dodn.commerce.core.enums.ReviewTargetType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class ReviewResponse {
    private final Long id;
    private final ReviewTargetType targetType;
    private final Long targetId;
    private final BigDecimal rate;
    private final String content;

    public static ReviewResponse of(Review review) {
        return new ReviewResponse(
                review.getId(),
                review.getTarget().getType(),
                review.getTarget().getId(),
                review.getContent().getRate(),
                review.getContent().getContent()
        );
    }

    public static List<ReviewResponse> of(List<Review> reviews) {
        return reviews.stream()
                .map(ReviewResponse::of)
                .collect(Collectors.toList());
    }
}
