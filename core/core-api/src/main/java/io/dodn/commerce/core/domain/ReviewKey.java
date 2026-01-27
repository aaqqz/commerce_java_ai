package io.dodn.commerce.core.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReviewKey {
    private final User user;
    private final String key;
}
