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

@Entity
@Table(
        name = "review",
        indexes = {
                @Index(name = "udx_user_review", columnList = "userId, reviewKey", unique = true)
        }
)
@Getter
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

    public ReviewEntity(
            Long userId,
            String reviewKey,
            ReviewTargetType targetType,
            Long targetId,
            BigDecimal rate,
            String content
    ) {
        this.userId = userId;
        this.reviewKey = reviewKey;
        this.targetType = targetType;
        this.targetId = targetId;
        this.rate = rate;
        this.content = content;
    }

    public void updateContent(BigDecimal rate, String content) {
        this.rate = rate;
        this.content = content;
    }
}
