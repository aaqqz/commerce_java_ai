package io.dodn.commerce.core.api.controller.v1.request;

import io.dodn.commerce.core.domain.CancelAction;

public record CancelRequest(
        String orderKey
) {
    public CancelAction toCancelAction() {
        return new CancelAction(orderKey);
    }
}
