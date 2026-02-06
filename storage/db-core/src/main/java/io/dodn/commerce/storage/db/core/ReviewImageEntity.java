package io.dodn.commerce.storage.db.core;

import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
        name = "review_image",
        indexes = {
                @Index(name = "idx_review_id", columnList = "reviewId")
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewImageEntity extends BaseEntity {
    private Long userId;
    private Long reviewId;
    private Long imageId;
    private String imageUrl;

    public static ReviewImageEntity create(
            Long userId,
            Long reviewId,
            Long imageId,
            String imageUrl
    ) {
        ReviewImageEntity reviewImage = new ReviewImageEntity();
        reviewImage.userId = userId;
        reviewImage.reviewId = reviewId;
        reviewImage.imageId = imageId;
        reviewImage.imageUrl = imageUrl;

        return reviewImage;
    }
}
