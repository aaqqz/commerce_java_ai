package io.dodn.commerce.core.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class Favorite {
    private final Long id;
    private final Long userId;
    private final Long productId;
    private final LocalDateTime favoritedAt;
}
