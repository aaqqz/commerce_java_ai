package io.dodn.commerce.core.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class QnA {
    private final Question question;
    private final Answer answer;
}
