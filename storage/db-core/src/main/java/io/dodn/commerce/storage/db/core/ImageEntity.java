package io.dodn.commerce.storage.db.core;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "image")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ImageEntity extends BaseEntity {
    private Long userId;
    private String imageUrl;
    private String originalFilename;

    public static ImageEntity create(Long userId, String imageUrl, String originalFilename) {
        ImageEntity image = new ImageEntity();
        image.userId = userId;
        image.imageUrl = imageUrl;
        image.originalFilename = originalFilename;

        return image;
    }
}
