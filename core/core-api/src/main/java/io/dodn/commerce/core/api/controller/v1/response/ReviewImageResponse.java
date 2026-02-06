package io.dodn.commerce.core.api.controller.v1.response;

import io.dodn.commerce.core.domain.ReviewImage;

public record ReviewImageResponse(
        Long id,
        String imageUrl
) {
    public static ReviewImageResponse of(ReviewImage reviewImage) {
        return new ReviewImageResponse(
                reviewImage.id(),
                reviewImage.imageUrl()
        );
    }
}
