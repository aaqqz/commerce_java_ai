package io.dodn.commerce.core.support.file;

import java.util.List;

public record ImageHandle(
        List<Long> addImageIds,
        List<Long> deleteImageIds
) {
    public Boolean hasImagesToAdd() {
        return addImageIds != null && !addImageIds.isEmpty();
    }

    public Boolean hasImagesToDelete() {
        return deleteImageIds != null && !deleteImageIds.isEmpty();
    }
}
