package io.dodn.commerce.storage.db.core;

import io.dodn.commerce.core.enums.ReviewTargetType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Entity
@Table(
        name = "review",
        indexes = {
                @Index(name = "udx_user_review", columnList = "userId, reviewKey", unique = true)
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewEntity extends BaseEntity {
    private Long userId;
    private String reviewKey;

    @Enumerated(EnumType.STRING)
    private ReviewTargetType targetType;

    private Long targetId;
    private BigDecimal rate;

    @Column(columnDefinition = "TEXT")
    private String content;

    public static ReviewEntity create(
            Long userId,
            String reviewKey,
            ReviewTargetType targetType,
            Long targetId,
            BigDecimal rate,
            String content
    ) {
        ReviewEntity review = new ReviewEntity();
        review.userId = userId;
        review.reviewKey = reviewKey;
        review.targetType = targetType;
        review.targetId = targetId;
        review.rate = rate;
        review.content = content;

        return review;
    }

    public void updateContent(BigDecimal rate, String content) {
        this.rate = rate;
        this.content = content;
    }
}
