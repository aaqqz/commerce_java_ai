package io.dodn.commerce.core.domain;

import io.dodn.commerce.ContextTest;
import io.dodn.commerce.core.enums.OrderState;
import io.dodn.commerce.core.enums.PaymentState;
import io.dodn.commerce.core.enums.PointType;
import io.dodn.commerce.core.support.error.CoreException;
import io.dodn.commerce.core.support.error.ErrorType;
import io.dodn.commerce.storage.db.core.OrderEntity;
import io.dodn.commerce.storage.db.core.OrderRepository;
import io.dodn.commerce.storage.db.core.PaymentEntity;
import io.dodn.commerce.storage.db.core.PaymentRepository;
import io.dodn.commerce.storage.db.core.PointBalanceEntity;
import io.dodn.commerce.storage.db.core.PointBalanceRepository;
import io.dodn.commerce.storage.db.core.PointHistoryEntity;
import io.dodn.commerce.storage.db.core.PointHistoryRepository;
import io.dodn.commerce.storage.db.core.TransactionHistoryRepository;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

public class PaymentServiceTest extends ContextTest {
    private final PaymentService paymentService;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final PointBalanceRepository pointBalanceRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final TransactionHistoryRepository transactionHistoryRepository;

    @Autowired
    public PaymentServiceTest(
            PaymentService paymentService,
            OrderRepository orderRepository,
            PaymentRepository paymentRepository,
            PointBalanceRepository pointBalanceRepository,
            PointHistoryRepository pointHistoryRepository,
            TransactionHistoryRepository transactionHistoryRepository
    ) {
        this.paymentService = paymentService;
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
        this.pointBalanceRepository = pointBalanceRepository;
        this.pointHistoryRepository = pointHistoryRepository;
        this.transactionHistoryRepository = transactionHistoryRepository;
    }

    @Test
    @Transactional
    void 결제_성공_시_포인트_차감_및_적립과_히스토리가_기록된다() {
        // given
        Long userId = 1234L;
        BigDecimal initialPoint = BigDecimal.valueOf(5000);
        BigDecimal usePoint = BigDecimal.valueOf(1200);
        BigDecimal orderPrice = BigDecimal.valueOf(10000);
        BigDecimal couponDiscount = BigDecimal.ZERO;
        BigDecimal paidAmount = orderPrice.subtract(couponDiscount.add(usePoint));
        String orderKey = "ORDER-KEY-123";

        // user point balance
        pointBalanceRepository.save(PointBalanceEntity.create(userId, initialPoint));

        // order in CREATED
        OrderEntity order = orderRepository.save(
                OrderEntity.create(
                        userId,
                        orderKey,
                        "테스트 주문",
                        orderPrice,
                        OrderState.CREATED
                )
        );

        // payment READY
        PaymentEntity payment = paymentRepository.save(
                PaymentEntity.create(
                        userId,
                        order.getId(),
                        orderPrice,
                        0L,
                        couponDiscount,
                        usePoint,
                        paidAmount,
                        PaymentState.READY
                )
        );

        // when
        Long resultPaymentId = paymentService.success(orderKey, "PG-EXT-KEY", paidAmount);

        // then
        // payment updated
        PaymentEntity updatedPayment = paymentRepository.findById(resultPaymentId).orElseThrow();
        assertThat(updatedPayment.getState()).isEqualTo(PaymentState.SUCCESS);
        assertThat(updatedPayment.getExternalPaymentKey()).isEqualTo("PG-EXT-KEY");

        // order updated
        OrderEntity updatedOrder = orderRepository.findById(order.getId()).orElseThrow();
        assertThat(updatedOrder.getState()).isEqualTo(OrderState.PAID);

        // point balance: initial - usePoint + earn(PAYMENT)
        PointBalanceEntity balance = pointBalanceRepository.findByUserId(userId).get();
        BigDecimal expectedBalance = initialPoint.subtract(usePoint).add(PointAmount.PAYMENT);
        assertThat(balance.getBalance()).isEqualByComparingTo(expectedBalance);

        // point history should have 2 entries: -usePoint, +earn
        List<PointHistoryEntity> histories = pointHistoryRepository.findByUserId(userId);
        histories.sort(Comparator.comparing(PointHistoryEntity::getId));
        assertThat(histories).hasSize(2);
        assertThat(histories.get(0).getType()).isEqualByComparingTo(PointType.PAYMENT);
        assertThat(histories.get(0).getReferenceId()).isEqualByComparingTo(resultPaymentId);
        assertThat(histories.get(0).getAmount()).isEqualByComparingTo(usePoint.negate());
        assertThat(histories.get(0).getBalanceAfter()).isEqualByComparingTo(initialPoint.subtract(usePoint));
        assertThat(histories.get(1).getType()).isEqualByComparingTo(PointType.PAYMENT);
        assertThat(histories.get(1).getReferenceId()).isEqualByComparingTo(resultPaymentId);
        assertThat(histories.get(1).getAmount()).isEqualByComparingTo(PointAmount.PAYMENT);
        assertThat(histories.get(1).getBalanceAfter()).isEqualByComparingTo(expectedBalance);

        // transaction history saved for payment
        assertThat(transactionHistoryRepository.findAll()).anySatisfy(tx -> {
            assertThat(tx.getPaymentId()).isEqualTo(payment.getId());
            assertThat(tx.getUserId()).isEqualTo(userId);
            assertThat(tx.getOrderId()).isEqualTo(order.getId());
            assertThat(tx.getExternalPaymentKey()).isEqualTo("PG-EXT-KEY");
            assertThat(tx.getAmount()).isEqualByComparingTo(paidAmount);
        });
    }
}
