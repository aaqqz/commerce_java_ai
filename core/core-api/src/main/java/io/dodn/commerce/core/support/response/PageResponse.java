package io.dodn.commerce.core.support.response;

import java.util.List;

public record PageResponse<T>(
        List<T> content,
        Boolean hasNext
) {

}
