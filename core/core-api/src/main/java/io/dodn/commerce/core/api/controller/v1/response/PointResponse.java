package io.dodn.commerce.core.api.controller.v1.response;

import io.dodn.commerce.core.domain.PointBalance;
import io.dodn.commerce.core.domain.PointHistory;
import java.math.BigDecimal;
import java.util.List;

public record PointResponse(
        Long userId,
        BigDecimal balance,
        List<PointHistoryResponse> histories
) {
    public static PointResponse of(PointBalance balance, List<PointHistory> histories) {
        List<PointHistoryResponse> mapped = histories.stream()
                .map(history -> new PointHistoryResponse(
                        history.type(),
                        history.amount(),
                        history.appliedAt()
                ))
                .toList();
        return new PointResponse(
                balance.userId(),
                balance.balance(),
                mapped
        );
    }
}
