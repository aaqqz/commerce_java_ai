package io.dodn.commerce.core.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Question {
    private final Long id;
    private final Long userId;
    private final String title;
    private final String content;
}
