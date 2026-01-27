package io.dodn.commerce.core.api.controller.v1.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ApplyFavoriteRequest {
    private Long productId;
    private ApplyFavoriteRequestType type;
}
