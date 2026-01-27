package io.dodn.commerce.core.support;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Getter
@AllArgsConstructor
public class OffsetLimit {
    private final int offset;
    private final int limit;

    public Pageable toPageable() {
        return PageRequest.of(offset / limit, limit);
    }
}
