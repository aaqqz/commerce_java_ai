package io.dodn.commerce.core.domain;

import io.dodn.commerce.core.support.error.CoreException;
import io.dodn.commerce.core.support.error.ErrorType;
import io.dodn.commerce.storage.db.core.PointBalanceEntity;
import io.dodn.commerce.storage.db.core.PointBalanceRepository;
import io.dodn.commerce.storage.db.core.PointHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PointService {
    private final PointBalanceRepository pointBalanceRepository;
    private final PointHistoryRepository pointHistoryRepository;

    public PointBalance balance(User user) {
        PointBalanceEntity found = pointBalanceRepository.findByUserId(user.id())
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND_DATA));;

        return new PointBalance(found.getUserId(), found.getBalance());
    }

    public List<PointHistory> histories(User user) {
        return pointHistoryRepository.findByUserId(user.id()).stream()
                .map(it -> new PointHistory(
                        it.getId(),
                        it.getUserId(),
                        it.getType(),
                        it.getReferenceId(),
                        it.getAmount(),
                        it.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }
}
