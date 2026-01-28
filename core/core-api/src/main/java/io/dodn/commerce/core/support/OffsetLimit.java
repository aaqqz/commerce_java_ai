package io.dodn.commerce.core.support;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public record OffsetLimit(
        Integer offset,
        Integer limit
) {
    public static OffsetLimit of(Integer offset, Integer limit) {
        return new OffsetLimit(offset, limit);
    }

    public Pageable toPageable() {
        return PageRequest.of(offset / limit, limit);
    }
}
