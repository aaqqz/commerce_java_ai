package io.dodn.commerce.core.api.controller.v1.request;

import io.dodn.commerce.core.domain.ReviewContent;
import io.dodn.commerce.core.support.error.CoreException;
import io.dodn.commerce.core.support.error.ErrorType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateReviewRequest {
    private BigDecimal rate;
    private String content;

    public ReviewContent toContent() {
        if (rate.compareTo(BigDecimal.ZERO) <= 0) {
            throw new CoreException(ErrorType.INVALID_REQUEST);
        }
        if (rate.compareTo(BigDecimal.valueOf(5.0)) > 0) {
            throw new CoreException(ErrorType.INVALID_REQUEST);
        }
        if (content.isEmpty()) {
            throw new CoreException(ErrorType.INVALID_REQUEST);
        }
        return new ReviewContent(rate, content);
    }
}
