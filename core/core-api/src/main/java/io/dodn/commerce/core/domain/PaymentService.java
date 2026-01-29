package io.dodn.commerce.core.domain;

import io.dodn.commerce.core.enums.EntityStatus;
import io.dodn.commerce.core.enums.OrderState;
import io.dodn.commerce.core.enums.PaymentMethod;
import io.dodn.commerce.core.enums.PaymentState;
import io.dodn.commerce.core.enums.PointType;
import io.dodn.commerce.core.enums.TransactionType;
import io.dodn.commerce.core.support.error.CoreException;
import io.dodn.commerce.core.support.error.ErrorType;
import io.dodn.commerce.storage.db.core.OrderEntity;
import io.dodn.commerce.storage.db.core.OrderRepository;
import io.dodn.commerce.storage.db.core.OwnedCouponEntity;
import io.dodn.commerce.storage.db.core.OwnedCouponRepository;
import io.dodn.commerce.storage.db.core.PaymentEntity;
import io.dodn.commerce.storage.db.core.PaymentRepository;
import io.dodn.commerce.storage.db.core.TransactionHistoryEntity;
import io.dodn.commerce.storage.db.core.TransactionHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final TransactionHistoryRepository transactionHistoryRepository;
    private final PointHandler pointHandler;
    private final OwnedCouponRepository ownedCouponRepository;

    @Transactional
    public Long createPayment(Order order, PaymentDiscount paymentDiscount) {
        PaymentEntity existingPayment = paymentRepository.findByOrderId(order.id())
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND_DATA));
        if (existingPayment.getState() == PaymentState.SUCCESS) {
            throw new CoreException(ErrorType.ORDER_ALREADY_PAID);
        }

        PaymentEntity payment = PaymentEntity.create(
                order.userId(),
                order.id(),
                order.totalPrice(),
                paymentDiscount.useOwnedCouponId(),
                paymentDiscount.couponDiscount(),
                paymentDiscount.usePoint(),
                paymentDiscount.paidAmount(order.totalPrice()),
                PaymentState.READY
        );
        return paymentRepository.save(payment).getId();
    }

    @Transactional
    public Long success(String orderKey, String externalPaymentKey, BigDecimal amount) {
        OrderEntity order = orderRepository.findByOrderKeyAndStateAndStatus(orderKey, OrderState.CREATED, EntityStatus.ACTIVE)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND_DATA));

        PaymentEntity payment = paymentRepository.findByOrderId(order.getId())
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND_DATA));

        if (!payment.getUserId().equals(order.getUserId())) {
            throw new CoreException(ErrorType.NOT_FOUND_DATA);
        }
        if (payment.getState() != PaymentState.READY) {
            throw new CoreException(ErrorType.PAYMENT_INVALID_STATE);
        }
        if (payment.getPaidAmount().compareTo(amount) != 0) {
            throw new CoreException(ErrorType.PAYMENT_AMOUNT_MISMATCH);
        }

        /**
         * NOTE: PG 승인 API 호출 => 성공 시 다음 로직으로 진행 | 실패 시 예외 발생
         */

        payment.success(
                externalPaymentKey,
                // NOTE: PG 승인 API 호출의 응답 값 중 `결제 수단` 넣기
                PaymentMethod.CARD,
                "PG 승인 API 호출의 응답 값 중 `승인번호` 넣기"
        );
        order.paid();

        if (payment.hasAppliedCoupon()) {
            ownedCouponRepository.findById(payment.getOwnedCouponId())
                    .ifPresent(OwnedCouponEntity::use);
        }

        pointHandler.deduct(new User(payment.getUserId()), PointType.PAYMENT, payment.getId(), payment.getUsedPoint());
        pointHandler.earn(new User(payment.getUserId()), PointType.PAYMENT, payment.getId(), PointAmount.PAYMENT);

        transactionHistoryRepository.save(
                TransactionHistoryEntity.create(
                        TransactionType.PAYMENT,
                        order.getUserId(),
                        order.getId(),
                        payment.getId(),
                        externalPaymentKey,
                        payment.getPaidAmount(),
                        "결제 성공",
                        payment.getPaidAt()
                )
        );
        return payment.getId();
    }

    public void fail(String orderKey, String code, String message) {
        OrderEntity order = orderRepository.findByOrderKeyAndStateAndStatus(orderKey, OrderState.CREATED, EntityStatus.ACTIVE)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND_DATA));

        PaymentEntity payment = paymentRepository.findByOrderId(order.getId())
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND_DATA));

        transactionHistoryRepository.save(
                TransactionHistoryEntity.create(
                        TransactionType.PAYMENT_FAIL,
                        order.getUserId(),
                        order.getId(),
                        payment.getId(),
                        "",
                        BigDecimal.valueOf(-1),
                        "[" + code + "] " + message,
                        LocalDateTime.now()
                )
        );
    }
}
