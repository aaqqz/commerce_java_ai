package io.dodn.commerce.core.api.controller.v1.request;

import io.dodn.commerce.core.domain.ReviewContent;
import io.dodn.commerce.core.support.error.CoreException;
import io.dodn.commerce.core.support.error.ErrorType;
import io.dodn.commerce.core.support.file.ImageHandle;

import java.math.BigDecimal;
import java.util.List;

public record UpdateReviewRequest(
        BigDecimal rate,
        String content,
        List<Long> images,
        List<Long> deleteImageIds
) {
    public ReviewContent toContent() {
        if (rate.compareTo(BigDecimal.ZERO) <= 0) throw new CoreException(ErrorType.INVALID_REQUEST);
        if (rate.compareTo(BigDecimal.valueOf(5.0)) > 0) throw new CoreException(ErrorType.INVALID_REQUEST);
        if (content.isEmpty()) throw new CoreException(ErrorType.INVALID_REQUEST);

        return new ReviewContent(rate, content);
    }

    public ImageHandle toImageHandle() {
        List<Long> list = images != null? images : List.of();
        if (list.size() > 5) throw new CoreException(ErrorType.INVALID_REQUEST);

        List<Long> deleteList = deleteImageIds != null ? deleteImageIds : List.of();
        return new  ImageHandle(list, deleteList);
    }
}
