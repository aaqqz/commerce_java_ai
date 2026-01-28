package io.dodn.commerce.core.support.response;

import java.util.List;

public record PageResponse<T>(
        List<T> content,
        Boolean hasNext
) {

    public static <T> PageResponse<T> of(List<T> content, Boolean hasNext) {
        return new PageResponse<>(content, hasNext);
    }
}
