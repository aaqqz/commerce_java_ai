package io.dodn.commerce.core.api.controller.v1;

import io.dodn.commerce.core.api.controller.v1.response.OwnedCouponResponse;
import io.dodn.commerce.core.domain.OwnedCouponService;
import io.dodn.commerce.core.domain.User;
import io.dodn.commerce.core.support.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CouponController {
    private final OwnedCouponService ownedCouponService;

    @PostMapping("/v1/coupons/{couponId}/download")
    public ApiResponse<Object> download(User user, @PathVariable Long couponId) {
        ownedCouponService.download(user, couponId);
        return ApiResponse.success();
    }

    @GetMapping("/v1/owned-coupons")
    public ApiResponse<List<OwnedCouponResponse>> getOwnedCoupons(User user) {
        var coupons = ownedCouponService.getOwnedCoupons(user);
        return ApiResponse.success(OwnedCouponResponse.of(coupons));
    }
}
