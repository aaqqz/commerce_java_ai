package io.dodn.commerce.core.api.controller.v1;

import io.dodn.commerce.core.api.controller.v1.request.AddReviewRequest;
import io.dodn.commerce.core.api.controller.v1.request.UpdateReviewRequest;
import io.dodn.commerce.core.api.controller.v1.response.ReviewResponse;
import io.dodn.commerce.core.domain.ReviewService;
import io.dodn.commerce.core.domain.ReviewTarget;
import io.dodn.commerce.core.domain.User;
import io.dodn.commerce.core.enums.ReviewTargetType;
import io.dodn.commerce.core.support.OffsetLimit;
import io.dodn.commerce.core.support.response.ApiResponse;
import io.dodn.commerce.core.support.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping("/v1/reviews")
    public ApiResponse<PageResponse<ReviewResponse>> getReviews(
            @RequestParam ReviewTargetType targetType,
            @RequestParam Long targetId,
            @RequestParam Integer offset,
            @RequestParam Integer limit
    ) {
        var page = reviewService.findReviews(new ReviewTarget(targetType, targetId), OffsetLimit.of(offset, limit));
        return ApiResponse.success(PageResponse.of(ReviewResponse.of(page.content()), page.hasNext()));
    }

    @PostMapping("/v1/reviews")
    public ApiResponse<Object> createReview(User user, @RequestBody AddReviewRequest request) {
        reviewService.addReview(user, request.toTarget(), request.toContent());
        return ApiResponse.success();
    }

    @PutMapping("/v1/reviews/{reviewId}")
    public ApiResponse<Object> updateReview(User user, @PathVariable Long reviewId, @RequestBody UpdateReviewRequest request) {
        reviewService.updateReview(user, reviewId, request.toContent());
        return ApiResponse.success();
    }

    @DeleteMapping("/v1/reviews/{reviewId}")
    public ApiResponse<Object> deleteReview(User user, @PathVariable Long reviewId) {
        reviewService.removeReview(user, reviewId);
        return ApiResponse.success();
    }
}
