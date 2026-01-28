package io.dodn.commerce.core.domain;

public record Question(
        Long id,
        Long userId,
        String title,
        String content
) {
}
