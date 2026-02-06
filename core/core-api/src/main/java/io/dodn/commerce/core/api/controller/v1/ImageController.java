package io.dodn.commerce.core.api.controller.v1;

import io.dodn.commerce.core.support.file.UploadResult;
import io.dodn.commerce.core.domain.User;
import io.dodn.commerce.core.support.file.ImageUploader;
import io.dodn.commerce.core.support.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class ImageController {
    private final ImageUploader imageUploader;

    @PostMapping("/v1/images/upload")
    public ApiResponse<UploadResult> uploadImage(User user, @RequestParam("file") MultipartFile file) {
        var uploadedImage = imageUploader.uploadImage(user, file);
        return ApiResponse.success(uploadedImage);
    }
}
