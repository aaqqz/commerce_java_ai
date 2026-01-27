package io.dodn.commerce.core.domain;

import io.dodn.commerce.core.enums.ReviewTargetType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReviewTarget {
    private final ReviewTargetType type;
    private final Long id;
}
