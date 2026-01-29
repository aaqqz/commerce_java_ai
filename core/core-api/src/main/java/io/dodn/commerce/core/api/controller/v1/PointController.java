package io.dodn.commerce.core.api.controller.v1;

import io.dodn.commerce.core.api.controller.v1.response.PointResponse;
import io.dodn.commerce.core.domain.PointService;
import io.dodn.commerce.core.domain.User;
import io.dodn.commerce.core.support.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PointController {
    private final PointService pointService;

    @GetMapping("/v1/point")
    public ApiResponse<PointResponse> getPoint(User user) {
        var balance = pointService.balance(user);
        var histories = pointService.histories(user);

        return ApiResponse.success(PointResponse.of(balance, histories));
    }
}
