package io.dodn.commerce.storage.db.core;

import io.dodn.commerce.core.enums.EntityStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuestionRepository extends JpaRepository<QuestionEntity, Long> {
    Optional<QuestionEntity> findByIdAndUserId(Long id, Long userId);
    Slice<QuestionEntity> findByProductIdAndStatus(Long productId, EntityStatus status, Pageable slice);
}
