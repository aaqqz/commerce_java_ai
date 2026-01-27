package io.dodn.commerce.core.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Answer {
    private final Long id;
    private final Long adminId;
    private final String content;

    public static final Answer EMPTY = new Answer(-1L, -1L, "");
}
