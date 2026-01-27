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
                        history.getType(),
                        history.getAmount(),
                        history.getAppliedAt()
                ))
                .toList();
        return new PointResponse(
                balance.getUserId(),
                balance.getBalance(),
                mapped
        );
    }
}
