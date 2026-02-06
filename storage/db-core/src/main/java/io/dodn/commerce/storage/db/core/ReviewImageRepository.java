package io.dodn.commerce.storage.db.core;

import io.dodn.commerce.core.enums.EntityStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewImageRepository extends JpaRepository<ReviewImageEntity, Long> {
    List<ReviewImageEntity> findByReviewIdInAndStatus(List<Long> reviewIds, EntityStatus status);
    List<ReviewImageEntity> findByReviewIdAndStatus(Long reviewId, EntityStatus status);
}
