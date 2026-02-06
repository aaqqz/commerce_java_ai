package io.dodn.commerce.core.domain;

import io.dodn.commerce.core.enums.EntityStatus;
import io.dodn.commerce.core.support.error.CoreException;
import io.dodn.commerce.core.support.error.ErrorType;
import io.dodn.commerce.core.support.file.ImageHandle;
import io.dodn.commerce.storage.db.core.*;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ReviewManager {
    private final ReviewRepository reviewRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final ImageRepository imageRepository;

    @Transactional
    public ReviewProcessResult add(ReviewKey reviewKey, ReviewTarget target, ReviewContent content, ImageHandle imageHandle) {
        ReviewEntity saved = reviewRepository.save(
                ReviewEntity.create(
                        reviewKey.user().id(),
                        reviewKey.key(),
                        target.type(),
                        target.id(),
                        content.rate(),
                        content.content()
                )
        );

        if (imageHandle.hasImagesToAdd()) {
            List<ImageEntity> uploadedImages = imageRepository.findByUserIdAndIdIn(saved.getUserId(), imageHandle.addImageIds());
            if (imageHandle.addImageIds().size() != uploadedImages.size()) throw new CoreException(ErrorType.INVALID_REQUEST);

            reviewImageRepository.saveAll(
                    uploadedImages.stream()
                            .map(it -> ReviewImageEntity.create(
                                    saved.getUserId(),
                                    saved.getId(),
                                    it.getId(),
                                    it.getImageUrl()
                            ))
                            .toList()
            );
        }

        return new ReviewProcessResult(saved.getId(), imageHandle.hasImagesToAdd() ? ReviewFormat.TEXT : ReviewFormat.IMAGE);
    }

    @Transactional
    public Long update(User user, Long reviewId, ReviewContent content, ImageHandle imageHandle) {
        ReviewEntity found = reviewRepository.findByIdAndUserId(reviewId, user.id())
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND_DATA));
        found.updateContent(content.rate(), content.content());

        // 기존 리뷰 이미지 불러오기
        var existingImages = reviewImageRepository.findByReviewIdAndStatus(reviewId, EntityStatus.ACTIVE);

        // 지정된 이미지 삭제
        if (imageHandle.hasImagesToDelete()) {
            existingImages.stream()
                    .filter(it -> it.getId().equals(found.getId()))
                    .forEach(reviewImageRepository::delete);
        }

        // 기존에 이미지가 있었는데 삭제 후 남은 이미지도 없고 새로운 이미지도 없으면 에러
        if (!existingImages.isEmpty()
                && !imageHandle.hasImagesToAdd()
                && existingImages.stream().noneMatch(ReviewImageEntity::isActive)
        ) {
            throw new CoreException(ErrorType.REVIEW_CANNOT_DELETE_ALL_IMAGES);
        }

        // 새 이미지 추가
        if (imageHandle.hasImagesToAdd()) {
            var uploadedImages = imageRepository.findByUserIdAndIdIn(found.getUserId(), imageHandle.addImageIds());
            if (imageHandle.addImageIds().size() != uploadedImages.size()) throw new CoreException(ErrorType.INVALID_REQUEST);
            reviewImageRepository.saveAll(
                    uploadedImages.stream()
                            .map(it ->
                                    ReviewImageEntity.create(
                                            found.getUserId(),
                                            found.getId(),
                                            it.getId(),
                                            it.getImageUrl()
                                    ))
                            .toList()
            );
        }

        return found.getId();
    }

    @Transactional
    public ReviewProcessResult delete(User user, Long reviewId) {
        ReviewEntity found = reviewRepository.findByIdAndUserId(reviewId, user.id())
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND_DATA));
        found.delete();

        // 이미지도 함께 삭제
        var images = reviewImageRepository.findByReviewIdAndStatus(reviewId, EntityStatus.ACTIVE);
        images.forEach(BaseEntity::delete);


        return new ReviewProcessResult(found.getId(), images.isEmpty() ? ReviewFormat.TEXT : ReviewFormat.IMAGE);
    }
}
