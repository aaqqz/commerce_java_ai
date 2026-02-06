package io.dodn.commerce.core.support.file;

import io.dodn.commerce.core.domain.User;
import io.dodn.commerce.storage.db.core.ImageEntity;
import io.dodn.commerce.storage.db.core.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

import static org.springframework.util.StringUtils.hasText;

@Component
@RequiredArgsConstructor
public class ImageUploader {
    private final ImageRepository imageRepository;

    public UploadResult uploadImage(User user, MultipartFile file) {
        // Mock S3 업로드 - 실제 S3 업로드 대신 Mock URL 생성
        var mockS3Url = uploadToS3Mock(file);

        // ImageEntity 저장
        var imageEntity = ImageEntity.create(
                user.id(),
                mockS3Url,
                hasText(file.getOriginalFilename())
                        ? file.getOriginalFilename()
                        : "unknown"
        );
        imageRepository.save(imageEntity);

        return new UploadResult(
                imageEntity.getId(),
                imageEntity.getImageUrl()
        );
    }

    private String uploadToS3Mock(MultipartFile file) {
        // Mock S3 업로드 구현
        // 실제로는 S3 클라이언트를 사용하여 업로드하지만, 여기서는 Mock URL 생성
        var uniqueId = UUID.randomUUID().toString();
        var filename = hasText(file.getOriginalFilename())
                ? file.getOriginalFilename()
                : "unknown";
        return "https://mock-s3-bucket.s3.amazonaws.com/images/" + uniqueId + "_" + filename;
    }
}
