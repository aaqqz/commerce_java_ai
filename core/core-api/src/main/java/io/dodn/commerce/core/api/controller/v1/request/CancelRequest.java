package io.dodn.commerce.core.api.controller.v1.request;

import io.dodn.commerce.core.domain.CancelAction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CancelRequest {
    private String orderKey;

    public CancelAction toCancelAction() {
        return new CancelAction(orderKey);
    }
}
