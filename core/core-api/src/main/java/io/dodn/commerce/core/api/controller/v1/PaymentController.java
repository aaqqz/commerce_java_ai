package io.dodn.commerce.core.api.controller.v1;

import io.dodn.commerce.core.api.controller.v1.request.CreatePaymentRequest;
import io.dodn.commerce.core.api.controller.v1.response.CreatePaymentResponse;
import io.dodn.commerce.core.domain.OrderService;
import io.dodn.commerce.core.domain.OwnedCouponService;
import io.dodn.commerce.core.domain.PaymentService;
import io.dodn.commerce.core.domain.PointService;
import io.dodn.commerce.core.domain.User;
import io.dodn.commerce.core.enums.OrderState;
import io.dodn.commerce.core.support.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;
    private final OrderService orderService;
    private final OwnedCouponService ownedCouponService;
    private final PointService pointService;

    @PostMapping("/v1/payments")
    public ApiResponse<CreatePaymentResponse> create(
            User user,
            @RequestBody CreatePaymentRequest request) {
        var order = orderService.getOrder(user, request.getOrderKey(), OrderState.CREATED);
        var ownedCoupons = ownedCouponService.getOwnedCouponsForCheckout(
                user,
                order.getItems().stream()
                        .map(io.dodn.commerce.core.domain.OrderItem::getProductId)
                        .collect(Collectors.toList())
        );
        var pointBalance = pointService.balance(user);
        Long id = paymentService.createPayment(
                order,
                request.toPaymentDiscount(ownedCoupons, pointBalance)
        );
        return ApiResponse.success(new CreatePaymentResponse(id));
    }

    @PostMapping("/v1/payments/callback/success")
    public ApiResponse<Object> callbackForSuccess(
            @RequestParam String orderId,
            @RequestParam String paymentKey,
            @RequestParam BigDecimal amount) {
        paymentService.success(orderId, paymentKey, amount);
        return ApiResponse.success();
    }

    @PostMapping("/v1/payments/callback/fail")
    public ApiResponse<Object> callbackForFail(
            @RequestParam String orderId,
            @RequestParam String code,
            @RequestParam String message) {
        paymentService.fail(orderId, code, message);
        return ApiResponse.success();
    }
}
