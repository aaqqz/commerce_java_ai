package io.dodn.commerce.core.domain;

import io.dodn.commerce.core.enums.EntityStatus;
import io.dodn.commerce.core.enums.OrderState;
import io.dodn.commerce.core.enums.PaymentState;
import io.dodn.commerce.core.enums.PointType;
import io.dodn.commerce.core.enums.TransactionType;
import io.dodn.commerce.core.support.error.CoreException;
import io.dodn.commerce.core.support.error.ErrorType;
import io.dodn.commerce.storage.db.core.CancelEntity;
import io.dodn.commerce.storage.db.core.CancelRepository;
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

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CancelService {
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final OwnedCouponRepository ownedCouponRepository;
    private final CancelRepository cancelRepository;
    private final TransactionHistoryRepository transactionHistoryRepository;
    private final PointHandler pointHandler;

    @Transactional
    public Long cancel(User user, CancelAction action) {
        OrderEntity order = orderRepository.findByOrderKeyAndStateAndStatus(action.orderKey(), OrderState.PAID, EntityStatus.ACTIVE)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND_DATA));

        if (!order.getUserId().equals(user.id())) {
            throw new CoreException(ErrorType.NOT_FOUND_DATA);
        }

        PaymentEntity payment = paymentRepository.findByOrderId(order.getId())
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND_DATA));

        if (payment.getState() != PaymentState.SUCCESS) {
            throw new CoreException(ErrorType.PAYMENT_INVALID_STATE);
        }

        /**
         * NOTE: PG 취소 API 호출 => 성공 시 다음 로직으로 진행 | 실패 시 예외 발생
         */

        order.canceled();

        if (payment.hasAppliedCoupon()) {
            ownedCouponRepository.findById(payment.getOwnedCouponId())
                    .ifPresent(OwnedCouponEntity::revert);
        }

        pointHandler.earn(new User(payment.getUserId()), PointType.PAYMENT, payment.getId(), payment.getUsedPoint());
        pointHandler.deduct(new User(payment.getUserId()), PointType.PAYMENT, payment.getId(), PointAmount.PAYMENT);

        CancelEntity cancel = cancelRepository.save(CancelEntity.create(
                payment.getUserId(),
                payment.getOrderId(),
                payment.getId(),
                payment.getOriginAmount(),
                payment.getOwnedCouponId(),
                payment.getCouponDiscount(),
                payment.getUsedPoint(),
                payment.getPaidAmount(),
                payment.getPaidAmount(),
                "PG_API_응답_취소_고유_값_저장",
                LocalDateTime.now()
        ));

        transactionHistoryRepository.save(TransactionHistoryEntity.create(
                TransactionType.CANCEL,
                payment.getUserId(),
                payment.getOrderId(),
                payment.getId(),
                payment.getExternalPaymentKey(),
                payment.getPaidAmount(),
                "취소 성공",
                cancel.getCanceledAt()
        ));

        return cancel.getId();
    }
}
