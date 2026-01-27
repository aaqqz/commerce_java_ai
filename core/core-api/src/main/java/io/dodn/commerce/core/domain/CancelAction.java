package io.dodn.commerce.core.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CancelAction {
    private final String orderKey;
}
