package io.dodn.commerce.storage.db.core;

import io.dodn.commerce.core.enums.EntityStatus;
import io.dodn.commerce.core.enums.ReviewTargetType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface
ReviewRepository extends JpaRepository<ReviewEntity, Long> {
    Optional<ReviewEntity> findByIdAndUserId(Long id, Long userId);
    List<ReviewEntity> findByUserIdAndReviewKeyIn(Long userId, Collection<String> reviewKey);
    List<ReviewEntity> findByTargetTypeAndTargetId(ReviewTargetType target, Long targetId);
    Slice<ReviewEntity> findByTargetTypeAndTargetIdAndStatus(ReviewTargetType target, Long targetId, EntityStatus status, Pageable slice);
}
