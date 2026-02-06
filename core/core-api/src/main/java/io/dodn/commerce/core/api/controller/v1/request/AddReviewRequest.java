package io.dodn.commerce.core.api.controller.v1.request;

import io.dodn.commerce.core.domain.ReviewContent;
import io.dodn.commerce.core.domain.ReviewTarget;
import io.dodn.commerce.core.enums.ReviewTargetType;
import io.dodn.commerce.core.support.error.CoreException;
import io.dodn.commerce.core.support.error.ErrorType;
import io.dodn.commerce.core.support.file.ImageHandle;

import java.math.BigDecimal;
import java.util.List;

public record AddReviewRequest(
        Long userId,
        ReviewTargetType targetType,
        Long targetId,
        BigDecimal rate,
        String content,
        List<Long> images
) {
    public ReviewTarget toTarget() {
        return new ReviewTarget(targetType, targetId);
    }

    public ReviewContent toContent() {
        if (rate.compareTo(BigDecimal.ZERO) <= 0) throw new CoreException(ErrorType.INVALID_REQUEST);
        if (rate.compareTo(BigDecimal.valueOf(5.0)) > 0) throw new CoreException(ErrorType.INVALID_REQUEST);
        if (content.isEmpty()) throw new CoreException(ErrorType.INVALID_REQUEST);

        return new ReviewContent(rate, content);
    }

    public ImageHandle toImageHandle() {
        List<Long> list = images != null ? images : List.of();
        if (list.size() > 5) throw new CoreException(ErrorType.INVALID_REQUEST);

        return new ImageHandle(list, List.of());
    }
}
