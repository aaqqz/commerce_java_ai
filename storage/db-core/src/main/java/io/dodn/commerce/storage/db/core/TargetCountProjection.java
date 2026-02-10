package io.dodn.commerce.storage.db.core;

public interface TargetCountProjection {
    Long getTargetId();

    @Deprecated
    default Long getProductId() {
        return getTargetId();
    }

    Long getCount();
}
