package io.dodn.commerce.core.api.controller.v1;

import io.dodn.commerce.core.api.controller.v1.request.CancelRequest;
import io.dodn.commerce.core.domain.CancelService;
import io.dodn.commerce.core.domain.User;
import io.dodn.commerce.core.support.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CancelController {
    private final CancelService cancelService;

    @PostMapping("/v1/cancel")
    public ApiResponse<Object> cancel(User user, @RequestBody CancelRequest request) {
        cancelService.cancel(user, request.toCancelAction());

        return ApiResponse.success();
    }
}
