package io.dodn.commerce.core.domain;

import io.dodn.commerce.core.support.error.CoreException;
import io.dodn.commerce.core.support.error.ErrorType;
import io.dodn.commerce.storage.db.core.ReviewEntity;
import io.dodn.commerce.storage.db.core.ReviewRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReviewManager {
    private final ReviewRepository reviewRepository;

    public Long add(ReviewKey reviewKey, ReviewTarget target, ReviewContent content) {
        ReviewEntity saved = reviewRepository.save(new ReviewEntity(
                reviewKey.getUser().getId(),
                reviewKey.getKey(),
                target.getType(),
                target.getId(),
                content.getRate(),
                content.getContent()
        ));
        return saved.getId();
    }

    @Transactional
    public Long update(User user, Long reviewId, ReviewContent content) {
        ReviewEntity found = reviewRepository.findByIdAndUserId(reviewId, user.getId());
        if (found == null) {
            throw new CoreException(ErrorType.NOT_FOUND_DATA);
        }
        found.updateContent(content.getRate(), content.getContent());
        return found.getId();
    }

    @Transactional
    public Long delete(User user, Long reviewId) {
        ReviewEntity found = reviewRepository.findByIdAndUserId(reviewId, user.getId());
        if (found == null) {
            throw new CoreException(ErrorType.NOT_FOUND_DATA);
        }
        found.delete();
        return found.getId();
    }
}
