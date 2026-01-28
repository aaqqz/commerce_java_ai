package io.dodn.commerce.core.support;

import java.util.List;

public record Page<T>(
        List<T> content,
        Boolean hasNext
) {

}
