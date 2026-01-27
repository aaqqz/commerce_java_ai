package io.dodn.commerce.core.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Review {
    private final Long id;
    private final Long userId;
    private final ReviewTarget target;
    private final ReviewContent content;
}
